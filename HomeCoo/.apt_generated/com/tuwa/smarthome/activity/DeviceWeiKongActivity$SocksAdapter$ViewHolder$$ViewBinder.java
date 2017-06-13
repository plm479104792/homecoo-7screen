// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceWeiKongActivity$SocksAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceWeiKongActivity.SocksAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231838, "field 'tvDevName'");
    target.tvDevName = finder.castView(view, 2131231838, "field 'tvDevName'");
    view = finder.findRequiredView(source, 2131231837, "field 'tvDevSite'");
    target.tvDevSite = finder.castView(view, 2131231837, "field 'tvDevSite'");
    view = finder.findRequiredView(source, 2131231839, "field 'tgSock'");
    target.tgSock = finder.castView(view, 2131231839, "field 'tgSock'");
  }

  @Override public void unbind(T target) {
    target.tvDevName = null;
    target.tvDevSite = null;
    target.tgSock = null;
  }
}
