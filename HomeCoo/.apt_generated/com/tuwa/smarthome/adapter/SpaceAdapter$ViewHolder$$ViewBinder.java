// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SpaceAdapter$ViewHolder$$ViewBinder<T extends com.tuwa.smarthome.adapter.SpaceAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131231836, "field 'imSetting'");
    target.imSetting = finder.castView(view, 2131231836, "field 'imSetting'");
    view = finder.findRequiredView(source, 2131231892, "field 'tvSpacename'");
    target.tvSpacename = finder.castView(view, 2131231892, "field 'tvSpacename'");
  }

  @Override public void unbind(T target) {
    target.imSetting = null;
    target.tvSpacename = null;
  }
}
