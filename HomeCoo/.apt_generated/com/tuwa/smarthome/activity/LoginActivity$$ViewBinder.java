// Generated code from Butter Knife. Do not modify!
package com.tuwa.smarthome.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginActivity$$ViewBinder<T extends com.tuwa.smarthome.activity.LoginActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230985, "field 'cbRemenberPwd'");
    target.cbRemenberPwd = finder.castView(view, 2131230985, "field 'cbRemenberPwd'");
    view = finder.findRequiredView(source, 2131230981, "field 'tvLoginExit' and method 'exit'");
    target.tvLoginExit = finder.castView(view, 2131230981, "field 'tvLoginExit'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.exit();
        }
      });
    view = finder.findRequiredView(source, 2131230987, "field 'mBtnRegister' and method 'userRegister'");
    target.mBtnRegister = finder.castView(view, 2131230987, "field 'mBtnRegister'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.userRegister();
        }
      });
  }

  @Override public void unbind(T target) {
    target.cbRemenberPwd = null;
    target.tvLoginExit = null;
    target.mBtnRegister = null;
  }
}
