// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class CameraKindsActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.CameraKindsActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230902, "field 'gvCameraKind' and method 'devwideClick'");
    target.gvCameraKind = finder.castView(view, 2131230902, "field 'gvCameraKind'");
    ((android.widget.AdapterView<?>) view).setOnItemClickListener(
      new android.widget.AdapterView.OnItemClickListener() {
        @Override public void onItemClick(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.devwideClick(p2);
        }
      });
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
    view = finder.findRequiredView(source, 2131231816, "field 'rg_navi_tab'");
    target.rg_navi_tab = finder.castView(view, 2131231816, "field 'rg_navi_tab'");
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
    view = finder.findRequiredView(source, 2131231818, "method 'naviSceneClick'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.naviSceneClick();
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
    target.gvCameraKind = null;
    target.tvBack = null;
    target.rg_navi_tab = null;
    target.tvbttomNetwork = null;
    target.tvExit = null;
    target.tvTitle = null;
  }
}
