package br.com.brunosan.mytodolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class UtilsCopyProperties {
    
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }
    
    public static String[] getNullPropertyNames(Object source) {
        // usando interface para acessar as propriedades de um obj
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] properties = src.getPropertyDescriptors();
        
        Set<String> emptyNames = new HashSet<>();
        
        for (PropertyDescriptor property : properties) {
            Object srcValue = src.getPropertyValue(property.getName());
            if (srcValue == null) {
                emptyNames.add(property.getName());
            }
        }
        
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
