package com.pgi.convergencemeetings.models;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nnennaiheke on 8/4/17.
 */

public class GreenConverter implements PropertyConverter<List, String> {

    @Override
    public List convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            List lista = Arrays.asList(databaseValue.split(","));
            return lista;
        }
    }

    @Override
    public String convertToDatabaseValue(List entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object link : entityProperty) {
                sb.append(link);
                sb.append(",");
            }
            return sb.toString();
        }


    }

}


