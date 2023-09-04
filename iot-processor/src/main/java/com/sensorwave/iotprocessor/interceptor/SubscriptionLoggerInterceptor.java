package com.sensorwave.iotprocessor.interceptor;

import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@LoggedSubscription
@Priority(2020)
@Interceptor
public class SubscriptionLoggerInterceptor {

    @AroundInvoke
    Object logInvocation(InvocationContext context) throws Exception {
        final Object[] parameters = context.getParameters();
        final String topic = (String) parameters[0];
        Log.info("Subscribing to topic " + topic);
        Object ret = context.proceed();
        return ret;
    }
}