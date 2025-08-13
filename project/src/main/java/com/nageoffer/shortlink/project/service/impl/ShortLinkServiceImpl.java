package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.common.enums.ValiDateTypeEnum;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortLinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import com.nageoffer.shortlink.project.util.LinkUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.project.common.constance.RedisKeyConstant.*;


/**
 * 短链接接口实现层
 * @author 20784
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper,ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateBloomFilter;
    private final ShortLinkMapper shortLinkMapper;
    private final RedissonClient redissonClient;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private final String createShortLinkDefaultDomain = "http://anthony.cn";

    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;

        // 先检查布隆过滤器
        boolean contains = shortUriCreateBloomFilter.contains(fullShortUrl);
        if (!contains) {
            response.sendRedirect("/page/notfound");
            return;
        }

        String gotoShortKey = String.format(GOTO_SHORT_LINK_KEY, fullShortUrl);
        String gotoNullKey = String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl);

        // 加锁防止缓存击穿
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            // 运行 Lua 脚本
            DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("atomic_restore_url.lua")));
            redisScript.setResultType(List.class);

            // 初始执行（只读，不写）
            List<Object> result = stringRedisTemplate.execute(
                    redisScript,
                    Arrays.asList(gotoShortKey, gotoNullKey),
                    "-", "60" // 默认先传空值，后面如果需要会再次调用写入
            );

            if ("HIT".equals(result.get(0))) {
                response.sendRedirect((String) result.get(1));
                return;
            } else if ("NULL".equals(result.get(0))) {
                response.sendRedirect("/page/notfound");
                return;
            }

            // Redis 没有，去 DB 查询
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                // DB 也没查到，缓存空值
                stringRedisTemplate.execute(
                        redisScript,
                        Arrays.asList(gotoShortKey, gotoNullKey),
                        "-", "60"
                );
                response.sendRedirect("/page/notfound");
                return;
            }

            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(linkQueryWrapper);

            if (shortLinkDO != null) {
                // 检查是否过期
                if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date())) {
                    stringRedisTemplate.execute(
                            redisScript,
                            Arrays.asList(gotoShortKey, gotoNullKey),
                            "-", "60"
                    );
                    response.sendRedirect("/page/notfound");
                    return;
                }

                // 写入正常 URL
                stringRedisTemplate.execute(
                        redisScript,
                        Arrays.asList(gotoShortKey, gotoNullKey),
                        shortLinkDO.getOriginUrl(), "60"
                );
                response.sendRedirect(shortLinkDO.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;

        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setDelFlag(0);
        shortLinkDO.setDelTime(0L);
//        shortLinkDO.setFavicon(getFavicon(requestParam.getOriginUrl()));
        shortLinkDO.setEnableStatus(0);

        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();

        try{
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        }catch (DuplicateKeyException ex){
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                            .eq(ShortLinkDO::getFullShortUrl,fullShortUrl);
            ShortLinkDO shortLinkDO1 = baseMapper.selectOne(queryWrapper);
            if(shortLinkDO1 != null){
                log.error("短链接:{} 重复生成入库",fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }
        }
        //缓存预热
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY,fullShortUrl),
                shortLinkDO.getOriginUrl(),
                LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()),TimeUnit.MILLISECONDS);
        shortUriCreateBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .build();
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam){
        int customGenerateCount = 0;
        String shortUri;
        while(true){
            if(customGenerateCount > 10){
                throw new ServiceException("短链接生成失败,请稍后再试!");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += System.currentTimeMillis();
            shortUri = HashUtil.hashToBase62(originUrl);
            if(!shortUriCreateBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)){
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortlink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid,requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus,0)
                .eq(ShortLinkDO::getDelFlag,0);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        return shortLinkMapper.groupShortLinkCount(requestParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid,requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl,requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag,0)
                .eq(ShortLinkDO::getEnableStatus,0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if(ObjectUtils.isNull(hasShortLinkDO)){
            throw new ClientException("短链接记录不存在!");
        }
        if(Objects.equals(hasShortLinkDO.getGid(),requestParam.getGid())){
            //如果修改后分组 gid 不变,不涉及到分片变更,可以简单处理,直接更新即可
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid,requestParam.getGid())
                        .eq(ShortLinkDO::getFullShortUrl,requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getEnableStatus,0)
                        .eq(ShortLinkDO::getDelFlag,0)
                        .set(Objects.equals(requestParam.getValidDateType(), ValiDateTypeEnum.PERMANENT.getType()),ShortLinkDO::getValidDate,null);
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .domain(hasShortLinkDO.getDomain())
                        .shortUri(hasShortLinkDO.getShortUri())
                        .clickNum(hasShortLinkDO.getClickNum())
                        .favicon(hasShortLinkDO.getFavicon())
                        .createdType(hasShortLinkDO.getCreatedType())
                        .gid(requestParam.getGid())
                        .originUrl(requestParam.getOriginUrl())
                        .description(requestParam.getDescription())
                        .validDate(requestParam.getValidDate())
                        .validDateType(requestParam.getValidDateType())
                        .favicon(hasShortLinkDO.getFavicon())
                        .build();
                baseMapper.update(shortLinkDO,updateWrapper);
            }else{
                //gid变了,涉及到分片切换,需要为mysql加上读写锁,写的过程中阻塞别的线程获取读锁和写锁
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY,requestParam.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            rLock.lock();
            try {
                LambdaUpdateWrapper<ShortLinkDO> linkUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid,hasShortLinkDO.getGid())
                        .eq(ShortLinkDO::getFullShortUrl,requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getEnableStatus,0)
                        .eq(ShortLinkDO::getDelFlag,0)
                        .eq(ShortLinkDO::getDelTime,0L);
                //删除存在于原分组中的旧短链接记录(软删除)
                ShortLinkDO delShortLinkDO = ShortLinkDO.builder()
                        .delTime(System.currentTimeMillis())
                        .delFlag(1)
                        .build();
                baseMapper.update(delShortLinkDO,linkUpdateWrapper);
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .domain(createShortLinkDefaultDomain)
                        .originUrl(requestParam.getOriginUrl())
                        .gid(requestParam.getGid())
                        .createdType(hasShortLinkDO.getCreatedType())
                        .gid(requestParam.getGid())
                        .validDate(requestParam.getValidDate())
                        .validDateType(requestParam.getValidDateType())
                        .description(requestParam.getDescription())
                        .shortUri(hasShortLinkDO.getShortUri())
                        .enableStatus(hasShortLinkDO.getEnableStatus())
                        .favicon(hasShortLinkDO.getFavicon())
                        .fullShortUrl(hasShortLinkDO.getFullShortUrl())
                        .favicon(hasShortLinkDO.getFavicon())
                        .delFlag(0)
                        .delTime(0L)
                        .build();
                //再插入新的
                baseMapper.insert(shortLinkDO);
            }finally {
                rLock.unlock();
            }
        }
    }

    /**
     * 获取网站的favicon图标链接
     * @param url 网站的URL
     * @return favicon图标链接，如果不存在则返回nulL
     */
    @SneakyThrows
    private String getFavicon(String url) {
        //创建URL对象
        URL targetUrl = new URL(url);
        //打开连接
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        // 禁止自动处理重定向
        connection.setInstanceFollowRedirects(false);
        // 设置请求方法为GET
        connection.setRequestMethod("GET");
        //连接
        connection.connect();
        //获取响应码
        int responseCode = connection.getResponseCode();
        // 如果是重定向响应码
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            //获取重定向的URL
            String redirectUrl = connection.getHeaderField("Location");
            //如果重定向URL不为空
            if (redirectUrl != null) {
                // 创建新的URL对象
                URL newUrl = new URL(redirectUrl);//打开新的连接
                connection = (HttpURLConnection) newUrl.openConnection();//设置请求方法为GET
                connection.setRequestMethod("GET");//连接
                connection.connect();//获取新的响应码
                responseCode = connection.getResponseCode();
            }
        }
        // 如果响应码为200(HTTP_OK)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return connection.getHeaderField("Favicon");
        }else{
            return null;
        }

    }
}
