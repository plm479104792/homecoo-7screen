// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SceneModelActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.SceneModelActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231045, "field 'gvScene'");
    target.gvScene = finder.castView(view, 2131231045, "field 'gvScene'");
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
  }

  @Override public void unbind(T target) {
    target.gvScene = null;
    target.tvbttomNetwork = null;
    target.tvExit = null;
    target.tvTitle = null;
    target.tvBack = null;
  }
}
