// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SpaceManegeActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.SpaceManegeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231869, "field 'tvtitle'");
    target.tvtitle = finder.castView(view, 2131231869, "field 'tvtitle'");
    view = finder.findRequiredView(source, 2131231061, "field 'gvSpace'");
    target.gvSpace = finder.castView(view, 2131231061, "field 'gvSpace'");
    view = finder.findRequiredView(source, 2131231870, "field 'tvSubmit'");
    target.tvSubmit = finder.castView(view, 2131231870, "field 'tvSubmit'");
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
  }

  @Override public void unbind(T target) {
    target.tvtitle = null;
    target.gvSpace = null;
    target.tvSubmit = null;
    target.tvBack = null;
  }
}
