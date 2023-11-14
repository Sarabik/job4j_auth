package ru.job4j.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NonNullFieldValueReplacer {
    public static <T> void updateAllNonNullFields(T currentObj, T newObj) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = currentObj.getClass().getDeclaredMethods();
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get") || methodName.startsWith("set")) {
                methodMap.put(methodName, method);
            }
        }
        for (String methodName : methodMap.keySet()) {
            if (methodName.startsWith("get")) {
                Method getMethod = methodMap.get(methodName);
                Method setMethod = methodMap.get(methodName.replace(
                        "get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Impossible invoke set method from object : "
                                    + currentObj + ", Check set and get pairs.");
                }
                Object newValue = getMethod.invoke(newObj);
                if (newValue != null) {
                    setMethod.invoke(currentObj, newValue);
                }
            }
        }
    }
}
