// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SceneManegeActivity$SceneAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.activity.SceneManegeActivity.SceneAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231836, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231836, "field 'imSetting'");
    view = finder.findRequiredView(source, 2131231877, "field 'tvScenename'");
    target.tvScenename = finder.castView(view, 2131231877, "field 'tvScenename'");
  }

  @Override public void unbind(T target) {
    target.imSetting = null;
    target.tvScenename = null;
  }
}
