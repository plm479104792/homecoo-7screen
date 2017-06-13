// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceSensorActivity$DeviceAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.DeviceSensorActivity.DeviceAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231833, "field 'tvDevSite'");
    target.tvDevSite = finder.castView(view, 2131231833, "field 'tvDevSite'");
    view = finder.findRequiredView(source, 2131231836, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231836, "field 'imSetting'");
    view = finder.findRequiredView(source, 2131231834, "field 'tvDevName'");
    target.tvDevName = finder.castView(view, 2131231834, "field 'tvDevName'");
  }

  @Override public void unbind(T target) {
    target.tvDevSite = null;
    target.imSetting = null;
    target.tvDevName = null;
  }
}
