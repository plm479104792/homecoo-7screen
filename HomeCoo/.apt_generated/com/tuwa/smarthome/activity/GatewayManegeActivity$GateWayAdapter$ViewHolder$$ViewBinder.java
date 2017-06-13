// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class GatewayManegeActivity$GateWayAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.GatewayManegeActivity.GateWayAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231865, "field 'tgBtnSwitch'");
    target.tgBtnSwitch = finder.castView(view, 2131231865, "field 'tgBtnSwitch'");
    view = finder.findRequiredView(source, 2131231862, "field 'tvGatewayNO'");
    target.tvGatewayNO = finder.castView(view, 2131231862, "field 'tvGatewayNO'");
    view = finder.findRequiredView(source, 2131231863, "field 'tvGatewayIP'");
    target.tvGatewayIP = finder.castView(view, 2131231863, "field 'tvGatewayIP'");
    view = finder.findRequiredView(source, 2131231836, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231836, "field 'imSetting'");
  }

  @Override public void unbind(T target) {
    target.tgBtnSwitch = null;
    target.tvGatewayNO = null;
    target.tvGatewayIP = null;
    target.imSetting = null;
  }
}
