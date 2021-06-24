/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package plus.extvos.restlet.aspect;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plus.extvos.restlet.annotation.Limit;
import plus.extvos.restlet.exception.RestletException;
import plus.extvos.restlet.service.LimitCounterService;
import plus.extvos.restlet.utils.RequestContext;

import java.lang.reflect.Method;


/**
 * @author Mingcai SHEN
 */
@Aspect
@Component
public class LimitAspect {

    private static final Logger log = LoggerFactory.getLogger(LimitAspect.class);
    @Autowired(required = false)
    private LimitCounterService counterService;

    public LimitAspect() {

    }

    @Pointcut("@annotation(plus.extvos.restlet.annotation.Limit)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestContext ctx = RequestContext.probe();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        Limit limit = signatureMethod.getAnnotation(Limit.class);
        Limit.Type limitType = limit.limitType();
        String key = limit.key();
        if (StrUtil.isEmpty(key)) {
            if (limitType == Limit.Type.IPADDR) {
                key = ctx.getIpAddress();
            } else {
                key = signatureMethod.getName();
            }
        }

        ImmutableList<Object> keys = ImmutableList.of(StrUtil.join(limit.prefix(), "_", key, "_",
            ctx.getRequestURI().replaceAll("/", "_")));

        int count = 0;
        if (counterService != null) {
            count = counterService.count(keys, limit.count(), limit.period());
        } else {
            count = 1;
        }
        if (count <= limit.count()) {
//            log.info("第{}次访问key为 {}，描述为 [{}] 的接口", count, keys, limit.name());
            return joinPoint.proceed();
        } else {
            log.warn("第{}次访问key为 {}，描述为 [{}] 的接口", count, keys, limit.name());
            throw RestletException.forbidden("access limited");
        }
    }
}
