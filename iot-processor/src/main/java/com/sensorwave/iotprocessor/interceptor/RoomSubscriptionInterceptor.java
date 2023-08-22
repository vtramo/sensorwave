package com.sensorwave.iotprocessor.interceptor;

import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@LoggedRoomSubscription
@Priority(2020)
@Interceptor
public class RoomSubscriptionInterceptor {

    @AroundInvoke
    Object logInvocation(InvocationContext context) throws Exception {
        final Object[] parameters = context.getParameters();
        final String roomId = (String) parameters[0];
        Log.info("Subscribing to room " + roomId);
        Object ret = context.proceed();
        return ret;
    }
}