package com.khoray.cashbook.model;


import com.khoray.cashbook.ResourceTable;
import ohos.agp.utils.Color;

public class Const {
    public static String[] payType = new String[] {"餐饮", "交通", "服饰", "购物", "教育", "娱乐", "运动", "生活", "红包"};
    public static int[] payImg = new int[] {ResourceTable.Media_canyin,
            ResourceTable.Media_gongjiao,
            ResourceTable.Media_fushi,
            ResourceTable.Media_gouwucheman,
            ResourceTable.Media_jiaoyu,
            ResourceTable.Media_yule,
            ResourceTable.Media_yundong,
            ResourceTable.Media_shenghuo,
            ResourceTable.Media_hongbao
    };
    public static String[] incomeType = new String[] {"工资", "奖金", "人情", "红包", "转账", "报销", "退款", "生意", "其他"};
    public static int[] incomeImg = new int[] {ResourceTable.Media_gongzi,
            ResourceTable.Media_jiangjinxiangqing,
            ResourceTable.Media_renqing,
            ResourceTable.Media_hongbao,
            ResourceTable.Media_zhuanzhang,
            ResourceTable.Media_baoxiao,
            ResourceTable.Media_tuikuan,
            ResourceTable.Media_shangdian,
            ResourceTable.Media_qita
    };

    public static final long dayMilllis = 24 * 60 * 60 * 1000;
    public static Color zfbBlue = (new Color(Color.rgb(2, 172, 239)));

}
