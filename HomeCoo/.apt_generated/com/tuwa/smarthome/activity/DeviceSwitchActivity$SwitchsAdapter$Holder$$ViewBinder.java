// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceSwitchActivity$SwitchsAdapter$Holder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceSwitchActivity.SwitchsAdapter.Holder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231842, "field 'tgBtn3'");
    target.tgBtn3 = finder.castView(view, 2131231842, "field 'tgBtn3'");
    view = finder.findRequiredView(source, 2131231837, "field 'tvDevSite'");
    target.tvDevSite = finder.castView(view, 2131231837, "field 'tvDevSite'");
    view = finder.findRequiredView(source, 2131231843, "field 'tgBtn2'");
    target.tgBtn2 = finder.castView(view, 2131231843, "field 'tgBtn2'");
    view = finder.findRequiredView(source, 2131231841, "field 'tgBtn4'");
    target.tgBtn4 = finder.castView(view, 2131231841, "field 'tgBtn4'");
    view = finder.findRequiredView(source, 2131231840, "field 'sbLight'");
    target.sbLight = finder.castView(view, 2131231840, "field 'sbLight'");
    view = finder.findRequiredView(source, 2131231844, "field 'tgBtn1'");
    target.tgBtn1 = finder.castView(view, 2131231844, "field 'tgBtn1'");
    view = finder.findRequiredView(source, 2131231838, "field 'tvDevName'");
    target.tvDevName = finder.castView(view, 2131231838, "field 'tvDevName'");
  }

  @Override public void unbind(T target) {
    target.tgBtn3 = null;
    target.tvDevSite = null;
    target.tgBtn2 = null;
    target.tgBtn4 = null;
    target.sbLight = null;
    target.tgBtn1 = null;
    target.tvDevName = null;
  }
}
