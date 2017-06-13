// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DefenceAreaActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.DefenceAreaActivity> implements ViewBinder<T> {
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
    view = finder.findRequiredView(source, 2131230916, "field 'tvIndoorBufang' and method 'indoorBufangOnClick'");
    target.tvIndoorBufang = finder.castView(view, 2131230916, "field 'tvIndoorBufang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.indoorBufangOnClick();
        }
      });
    view = finder.findRequiredView(source, 2131230919, "field 'tvOutdoorChefang' and method 'outdoorChefangOnClick'");
    target.tvOutdoorChefang = finder.castView(view, 2131230919, "field 'tvOutdoorChefang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.outdoorChefangOnClick();
        }
      });
    view = finder.findRequiredView(source, 2131230917, "field 'tvIndoorChefang' and method 'indoorChefangOnClick'");
    target.tvIndoorChefang = finder.castView(view, 2131230917, "field 'tvIndoorChefang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.indoorChefangOnClick();
        }
      });
    view = finder.findRequiredView(source, 2131230918, "field 'tvOutdoorBufang' and method 'outdoorBufangOnClick'");
    target.tvOutdoorBufang = finder.castView(view, 2131230918, "field 'tvOutdoorBufang'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.outdoorBufangOnClick();
        }
      });
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
    view = finder.findRequiredView(source, 2131231869, "field 'tvTitle'");
    target.tvTitle = finder.castView(view, 2131231869, "field 'tvTitle'");
    view = finder.findRequiredView(source, 2131231816, "field 'rg_navi_tab'");
    target.rg_navi_tab = finder.castView(view, 2131231816, "field 'rg_navi_tab'");
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
    view = finder.findRequiredView(source, 2131231821, "method 'systemSet'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.systemSet();
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
  }

  @Override public void unbind(T target) {
    target.tvBack = null;
    target.tvbttomNetwork = null;
    target.tvIndoorBufang = null;
    target.tvOutdoorChefang = null;
    target.tvIndoorChefang = null;
    target.tvOutdoorBufang = null;
    target.tvExit = null;
    target.tvTitle = null;
    target.rg_navi_tab = null;
  }
}
