// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SetActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.SetActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231867, "field 'tvBack' and method 'back'");
    target.tvBack = finder.castView(view, 2131231867, "field 'tvBack'");
    view.setOnTouchListener(
      new android.view.View.OnTouchListener() {
        @Override public boolean onTouch(
          android.view.View p0,
          android.view.MotionEvent p1
        ) {
          return target.back();
        }
      });
    view = finder.findRequiredView(source, 2131231053, "field 'btGatewaySet' and method 'systemSet'");
    target.btGatewaySet = finder.castView(view, 2131231053, "field 'btGatewaySet'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.systemSet();
        }
      });
    view = finder.findRequiredView(source, 2131231819, "field 'tvbttomNetwork' and method 'networkSwitchClick'");
    target.tvbttomNetwork = finder.castView(view, 2131231819, "field 'tvbttomNetwork'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.networkSwitchClick();
        }
      });
    view = finder.findRequiredView(source, 2131231050, "field 'btSpaceManege' and method 'spaceManege'");
    target.btSpaceManege = finder.castView(view, 2131231050, "field 'btSpaceManege'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.spaceManege();
        }
      });
    view = finder.findRequiredView(source, 2131231869, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231869, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231870, "field 'tvExit' and method 'systemExit'");
    target.tvExit = finder.castView(view, 2131231870, "field 'tvExit'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.systemExit();
        }
      });
    view = finder.findRequiredView(source, 2131231820, "method 'DefenceAreaClick'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.DefenceAreaClick();
        }
      });
    view = finder.findRequiredView(source, 2131231818, "method 'sceneMode'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.sceneMode();
        }
      });
    view = finder.findRequiredView(source, 2131231817, "method 'spaceDeviceShow'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.spaceDeviceShow();
        }
      });
    view = finder.findRequiredView(source, 2131231051, "method 'gatewaySet'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.gatewaySet();
        }
      });
    view = finder.findRequiredView(source, 2131231048, "method 'deviceManege'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.deviceManege();
        }
      });
    view = finder.findRequiredView(source, 2131231054, "method 'sceneManege'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneManege();
        }
      });
    view = finder.findRequiredView(source, 2131231049, "method 'timeTaskManege'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.timeTaskManege();
        }
      });
    view = finder.findRequiredView(source, 2131231052, "method 'linkManege'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.linkManege();
        }
      });
  }

  @Override public void unbind(T target) {
    target.tvBack = null;
    target.btGatewaySet = null;
    target.tvbttomNetwork = null;
    target.btSpaceManege = null;
    target.tvtitle = null;
    target.tvExit = null;
  }
}
