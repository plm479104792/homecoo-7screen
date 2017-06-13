// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SceneQuickActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.SceneQuickActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230822, "field 'rbSceneQuick4' and method 'sceneQuick4'");
    target.rbSceneQuick4 = finder.castView(view, 2131230822, "field 'rbSceneQuick4'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneQuick4();
        }
      });
    view = finder.findRequiredView(source, 2131230817, "field 'rbSceneQuick'");
    target.rbSceneQuick = finder.castView(view, 2131230817, "field 'rbSceneQuick'");
    view = finder.findRequiredView(source, 2131231869, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231869, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231867, "field 'tvBack'");
    target.tvBack = finder.castView(view, 2131231867, "field 'tvBack'");
    view = finder.findRequiredView(source, 2131230818, "field 'rbSceneQuick1' and method 'sceneQuick1'");
    target.rbSceneQuick1 = finder.castView(view, 2131230818, "field 'rbSceneQuick1'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneQuick1();
        }
      });
    view = finder.findRequiredView(source, 2131230821, "field 'rbSceneQuick3' and method 'sceneQuick3'");
    target.rbSceneQuick3 = finder.castView(view, 2131230821, "field 'rbSceneQuick3'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneQuick3();
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
    view = finder.findRequiredView(source, 2131230819, "field 'rbSceneQuick2' and method 'sceneQuick2'");
    target.rbSceneQuick2 = finder.castView(view, 2131230819, "field 'rbSceneQuick2'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneQuick2();
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
    view = finder.findRequiredView(source, 2131231818, "method 'sceneMode'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneMode();
        }
      });
    view = finder.findRequiredView(source, 2131230820, "method 'sceneDefaultSet'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sceneDefaultSet();
        }
      });
  }

  @Override public void unbind(T target) {
    target.rbSceneQuick4 = null;
    target.rbSceneQuick = null;
    target.tvtitle = null;
    target.tvBack = null;
    target.rbSceneQuick1 = null;
    target.rbSceneQuick3 = null;
    target.tvbttomNetwork = null;
    target.rbSceneQuick2 = null;
  }
}
