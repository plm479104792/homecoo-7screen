// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.HomeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231868, "field 'ivLogo'");
    target.ivLogo = finder.castView(view, 2131231868, "field 'ivLogo'");
    view = finder.findRequiredView(source, 2131230979, "field 'tvhumidity'");
    target.tvhumidity = finder.castView(view, 2131230979, "field 'tvhumidity'");
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
    view = finder.findRequiredView(source, 2131230970, "field 'gvDevWideType' and method 'devwideClick'");
    target.gvDevWideType = finder.castView(view, 2131230970, "field 'gvDevWideType'");
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
    view = finder.findRequiredView(source, 2131231870, "field 'tvheadExit' and method 'systemExit'");
    target.tvheadExit = finder.castView(view, 2131231870, "field 'tvheadExit'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.systemExit();
        }
      });
    view = finder.findRequiredView(source, 2131231869, "field 'tvheadTitle'");
    target.tvheadTitle = finder.castView(view, 2131231869, "field 'tvheadTitle'");
    view = finder.findRequiredView(source, 2131230978, "field 'tvtemprature'");
    target.tvtemprature = finder.castView(view, 2131230978, "field 'tvtemprature'");
    view = finder.findRequiredView(source, 2131231867, "field 'tvBack'");
    target.tvBack = finder.castView(view, 2131231867, "field 'tvBack'");
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
    view = finder.findRequiredView(source, 2131231820, "method 'defenceArea'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.defenceArea();
        }
      });
  }

  @Override public void unbind(T target) {
    target.ivLogo = null;
    target.tvhumidity = null;
    target.tvbttomNetwork = null;
    target.gvDevWideType = null;
    target.tvheadExit = null;
    target.tvheadTitle = null;
    target.tvtemprature = null;
    target.tvBack = null;
  }
}
