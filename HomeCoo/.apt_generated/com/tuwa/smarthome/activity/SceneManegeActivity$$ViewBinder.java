// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SceneManegeActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.SceneManegeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231867, "field 'tvBack' and method 'back'");
    target.tvBack = finder.castView(view, 2131231867, "field 'tvBack'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.back();
        }
      });
    view = finder.findRequiredView(source, 2131231045, "field 'gvScene'");
    target.gvScene = finder.castView(view, 2131231045, "field 'gvScene'");
    view = finder.findRequiredView(source, 2131231869, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231869, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231870, "field 'tvSubmit' and method 'syncScene2Gateway'");
    target.tvSubmit = finder.castView(view, 2131231870, "field 'tvSubmit'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.syncScene2Gateway();
        }
      });
  }

  @Override public void unbind(T target) {
    target.tvBack = null;
    target.gvScene = null;
    target.tvtitle = null;
    target.tvSubmit = null;
  }
}
