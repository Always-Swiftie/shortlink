-- KEYS[1] = 正常短链缓存 key (GOTO_SHORT_LINK_KEY)
-- KEYS[2] = 空值缓存 key (GOTO_IS_NULL_SHORT_LINK_KEY)
-- ARGV[1] = originUrl (可以是 "-" 表示空值)
-- ARGV[2] = expireTime 秒（仅空值缓存用）

-- Step1: 检查正常缓存
local originUrl = redis.call("GET", KEYS[1])
if originUrl then
    return { "HIT", originUrl }
end

-- Step2: 检查空值缓存
local isNull = redis.call("GET", KEYS[2])
if isNull then
    return { "NULL", nil }
end

-- Step3: 写入缓存
    if ARGV[1] == "-" then
        redis.call("SETEX", KEYS[2], ARGV[2], "-")
        return { "NULL", nil }
    else
    redis.call("SET", KEYS[1], ARGV[1])
    return { "SET", ARGV[1] }
end
