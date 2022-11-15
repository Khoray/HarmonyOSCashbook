package com.khoray.cashbook.provider;

import com.khoray.cashbook.model.Const;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.text.Layout;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.utils.TextAlignment;
import ohos.app.Context;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class PageProvider extends PageSliderProvider {

    public static interface ClickedListener {
        public void click(int majorType, int minorType);
    }
    ClickedListener listener;
    Context ctx;

    public PageProvider(ClickedListener listener, Context ctx) {
        this.listener = listener;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        DirectionalLayout layout = new DirectionalLayout(ctx);
        layout.setWidth(MATCH_PARENT);
        layout.setHeight(MATCH_CONTENT);
        layout.setOrientation(Component.VERTICAL);
        layout.setPaddingTop(AttrHelper.vp2px(17, ctx));
        layout.setPaddingBottom(AttrHelper.vp2px(17, ctx));
//        ShapeElement ele = new ShapeElement();
//        ele.setRgbColor(new RgbColor(255,255,255, 52));
//        layout.setBackground(ele);
        int len;
        if(i == 0) {
            len = Const.payType.length;
        } else {
            len = Const.incomeType.length;
        }
        int width = componentContainer.getWidth();
        for(int k = 0; k < 100; k++) {
            if(k * 3 >= len) break;
            DirectionalLayout ly = new DirectionalLayout(ctx);
            ly.setWidth(MATCH_PARENT);
            ly.setHeight(MATCH_CONTENT);
            ly.setOrientation(Component.HORIZONTAL);
            ly.setAlignment(LayoutAlignment.CENTER);
            ly.setTotalWeight(3);
            ly.setMarginTop(AttrHelper.vp2px(5, ctx));
            for(int j = 0; j < 3; j++) {
                if(k * 3 + j >= len) break;
                DirectionalLayout du = new DirectionalLayout(ctx);
                du.setWidth(200);
                du.setAlignment(LayoutAlignment.CENTER);
                du.setHeight(MATCH_CONTENT);

                Image image = new Image(ctx);
                if(i == 0) {
                    image.setPixelMap(Const.payImg[j + k * 3]);
                } else {
                    image.setPixelMap(Const.incomeImg[j + k * 3]);
                }
                image.setScaleMode(Image.ScaleMode.STRETCH);
                image.setHeight(80);
                image.setWidth(80);

                du.addComponent(image);
                Text t = new Text(ctx);
                t.setWidth(200);
                t.setHeight(40);
                t.setTextSize(AttrHelper.vp2px(10, ctx));
                t.setTextAlignment(TextAlignment.CENTER);
                if(i == 0) {
                    t.setText(Const.payType[j + k * 3]);
                } else {
                    t.setText(Const.incomeType[j + k * 3]);
                }
                du.addComponent(t);
                int finalJ = k * 3 + j;
                du.setClickedListener((Component component) -> {
                    listener.click(i, finalJ);
                });
                ly.addComponent(du);
            }
            layout.addComponent(ly);
        }

        componentContainer.addComponent(layout);
        return layout;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {

    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return false;
    }
}
