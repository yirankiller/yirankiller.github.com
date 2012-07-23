package com.ikiller.yirankiller.util.string.bean;

import com.ikiller.yirankiller.util.string.FormatUtil;

/**
 * User: easliu
 * Date: 7/20/12
 * Version: 1.0
 */
@Stateless
public class FormatUtilBean implements FormatUtil {
    @Override
    public String formatDate(String date) {
        return "2012/07/11";
    }
}
