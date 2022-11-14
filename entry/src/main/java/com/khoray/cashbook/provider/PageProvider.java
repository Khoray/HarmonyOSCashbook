package com.khoray.cashbook.provider;

import com.khoray.cashbook.model.Const;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.text.Layout;
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
        DependentLayout layout = new DependentLayout(ctx);
        layout.setWidth(MATCH_PARENT);
        layout.setHeight(MATCH_CONTENT);
//        ShapeElement ele = new ShapeElement();
//        ele.setRgbColor(new RgbColor(255,255,255, 52));
//        layout.setBackground(ele);
        int len;
        if(i == 0) {
            len = Const.payType.length;
        } else {
            len = Const.incomeType.length;
        }

        for(int j = 0; j < len; j++) {
            Button btn = new Button(ctx);
            btn.setWidth(200);
            btn.setHeight(80);
            btn.setTextSize(AttrHelper.vp2px(17, ctx));
            if(i == 0) {
                btn.setText(Const.payType[j]);
            } else {
                btn.setText(Const.incomeType[j]);
            }

            btn.setMarginTop(j / 3 * 80);
            btn.setMarginLeft(j % 3 * 200);
            int finalJ = j;
            btn.setClickedListener((Component component) -> {
                listener.click(i, finalJ);
            });
            layout.addComponent(btn);
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
