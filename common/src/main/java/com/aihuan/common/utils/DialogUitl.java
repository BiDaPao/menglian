package com.aihuan.common.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatEditText;

import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aihuan.common.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;


/**
 * Created by cxf on 2017/8/8.
 */

public class DialogUitl {
    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_NUMBER = 1;
    public static final int INPUT_TYPE_NUMBER_PASSWORD = 2;
    public static final int INPUT_TYPE_TEXT_PASSWORD = 3;

    //第三方登录的时候用显示的dialog
    public static Dialog loginAuthDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_login_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 用于网络请求等耗时操作的LoadingDialog
     */
    public static Dialog loadingDialog(Context context, String text) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(text)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.text);
            if (titleView != null) {
                titleView.setText(text);
            }
        }
        return dialog;
    }


    public static Dialog loadingDialog(Context context) {
        return loadingDialog(context, "");
    }

    public static void showSimpleTipDialog(Context context, String content) {
        showSimpleTipDialog(context, null, content);
    }

    public static void showSimpleTipDialog(Context context, String title, String content) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_simple_tip);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(title)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            titleView.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            TextView contentTextView = (TextView) dialog.findViewById(R.id.content);
            contentTextView.setText(content);
        }
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSimpleDialog2(Context context, String content, final SimpleCallback simpleCallback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_simple2);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(content)) {
            TextView contentTextView = (TextView) dialog.findViewById(R.id.content);
            contentTextView.setText(content);
        }
        TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (simpleCallback != null) {
                    simpleCallback.onConfirmClick(dialog, "");
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSimpleDialog3(Context context, String content, String confirm, String cancel, final SimpleCallback simpleCallback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_simple3);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(content)) {
            TextView contentTextView = (TextView) dialog.findViewById(R.id.content);
            contentTextView.setText(content);
        }
        TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        if (!TextUtils.isEmpty(confirm)) {
            btn_confirm.setText(confirm);
        }
        if (!TextUtils.isEmpty(cancel)) {
            btn_cancel.setText(cancel);
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (simpleCallback != null) {
                    simpleCallback.onConfirmClick(dialog, "");
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static void showSimpleDialog(Context context, String content, SimpleCallback callback) {
        showSimpleDialog(context, content, true, callback);
    }

    public static void showSimpleDialog(Context context, boolean backgroundDimEnabled, String content, SimpleCallback callback) {
        new Builder(context)
                .setContent(content)
                .setBackgroundDimEnabled(backgroundDimEnabled)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleDialog(Context context, String content, boolean cancelable, SimpleCallback callback) {
        showSimpleDialog(context, null, content, cancelable, callback);
    }

    public static void showSimpleDialog(Context context, String title, String content, boolean cancelable, SimpleCallback callback) {
        new Builder(context)
                .setTitle(title)
                .setContent(content)
                .setCancelable(cancelable)
                .setClickCallback(callback)
                .build()
                .show();
    }


    public static void showSimpleInputDialog(Context context, String title, String hint, int inputType, int length, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleTipsDialog(Context context, String title, String content, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(false)
                .setInput(false)
                .setContent(content)
                .setBackgroundDimEnabled(true)
                .setClickCallback(callback)
                .build()
                .show();
    }


    public static void showSimpleInputDialog(Context context, String title, int inputType, int length, SimpleCallback callback) {
        showSimpleInputDialog(context, title, null, inputType, length, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, SimpleCallback callback) {
        showSimpleInputDialog(context, title, inputType, 0, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, SimpleCallback callback) {
        showSimpleInputDialog(context, title, INPUT_TYPE_TEXT, callback);
    }


    public static void showStringArrayDialog(Context context, Integer[] array, StringArrayDialogCallback callback) {
        showStringArrayDialog(context, array, true, callback);
    }

    public static void showStringArrayDialog(Context context, Integer[] array, boolean dark, StringArrayDialogCallback callback) {
        getStringArrayDialog(context, array, dark, callback).show();
    }


    public static void showTitleStringArrayDialog(Context context, Integer[] array, boolean dark, String title, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, dark ? R.style.dialog : R.style.dialog2);
        dialog.setContentView(R.layout.dialog_hastitle_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        TextView titleView = dialog.findViewById(R.id.title);
        titleView.setText(title);
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.bg_dialog_2);
        for (int i = 0, length = array.length; i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(50)));
            textView.setTextColor(0xffFF6262);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(bg);
            textView.setText(array[i]);
            textView.setTag(array[i]);
            textView.setOnClickListener(itemListener);
            container.addView(textView);
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static Dialog getStringArrayDialog(Context context, Integer[] array, boolean dark, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, dark ? R.style.dialog : R.style.dialog2);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.bg_dialog_2);
        for (int i = 0, length = array.length; i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(50)));
            textView.setTextColor(0xff323232);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(bg);
            textView.setText(array[i]);
            textView.setTag(array[i]);
            textView.setOnClickListener(itemListener);
            container.addView(textView);
            if (i != length - 1) {
                View v = new View(context);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(1)));
                v.setBackgroundColor(0xffe5e5e5);
                container.addView(v);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }


    public static Dialog getStringArrayDialog(Context context, SparseArray<String> array, boolean dark, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, dark ? R.style.dialog : R.style.dialog2);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.bg_dialog_2);
        for (int i = 0, length = array.size(); i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(50)));
            textView.setTextColor(0xff323232);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(bg);
            textView.setText(array.valueAt(i));
            textView.setTag(array.keyAt(i));
            textView.setOnClickListener(itemListener);
            container.addView(textView);
            if (i != length - 1) {
                View v = new View(context);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(1)));
                v.setBackgroundColor(0xffe5e5e5);
                container.addView(v);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static void showStringArrayDialog(Context context, SparseArray<String> array, boolean dark, StringArrayDialogCallback callback) {
        getStringArrayDialog(context, array, dark, callback).show();
    }

    public static void showStringArrayDialog(Context context, SparseArray<String> array, StringArrayDialogCallback callback) {
        showStringArrayDialog(context, array, true, callback);
    }


    /**
     * 城市选择
     */
    public static void showCityChooseDialog(Activity activity, ArrayList<Province> list,
                                            boolean hideCounty,
                                            String province, String city, String district,
                                            AddressPicker.OnAddressPickListener listener) {
        AddressPicker picker = new AddressPicker(activity, list);
        picker.setTextColor(0xff323232);
        picker.setDividerColor(0xffdcdcdc);
        picker.setAnimationStyle(R.style.bottomToTopAnim);
        picker.setCancelTextColor(0xff969696);
        picker.setSubmitTextColor(0xff7014E2);
        picker.setTopLineColor(0xfff5f5f5);
        picker.setTopBackgroundColor(0xfff5f5f5);
        picker.setHeight(DpUtil.dp2px(250));
        picker.setOffset(5);
        picker.setHideProvince(false);
        picker.setHideCounty(false);
        picker.setColumnWeight(3 / 9.0f, 3 / 9.0f, 3 / 9.0f);
        if (TextUtils.isEmpty(province)) {
            province = "北京市";
        }
        if (TextUtils.isEmpty(city)) {
            city = "北京市";
        }
        if (TextUtils.isEmpty(district)) {
            district = "东城区";
        }
        picker.setSelectedItem(province, city, district);
        picker.setHideCounty(hideCounty);
        picker.setOnAddressPickListener(listener);
        picker.show();
    }

    /**
     * 星座
     */
    public static void showXinZuoDialog(Activity activity, String[] xinZuoArray, OptionPicker.OnOptionPickListener listener) {
        OptionPicker picker = new OptionPicker(activity, xinZuoArray);
        picker.setCycleDisable(false);//不禁用循环
        picker.setTopBackgroundColor(0xfff5f5f5);
        picker.setHeight(DpUtil.dp2px(250));
        picker.setTopLineVisible(false);
        picker.setCancelTextColor(0xff969696);
        picker.setSubmitTextColor(0xff7014E2);
        picker.setTextColor(0xff323232, 0xffc8c8c8);
        picker.setTextSize(16);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setColor(0xffdcdcdc);//线颜色
        config.setRatio(1);//线比率
        picker.setDividerConfig(config);
        picker.setBackgroundColor(0xffffffff);
        picker.setSelectedIndex(0);
        picker.setAnimationStyle(R.style.bottomToTopAnim);
        picker.setCanceledOnTouchOutside(true);
        picker.setOnOptionPickListener(listener);
        picker.show();
    }


    public static void showDatePickerDialog(Context context,
                                            int year, int month, int day,
                                            final DataPickerCallback callback) {
        L.e("年： " + year + "   月：" + month + "  day: " + day);
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        final Calendar c = Calendar.getInstance();
        if (year <= 0) {
            year = c.get(Calendar.YEAR);
        }
        if (month <= 0) {
            month = c.get(Calendar.MONTH);
        }
        if (day <= 0) {
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_confirm) {
                    if (callback != null) {
                        if (c.getTime().getTime() > new Date().getTime()) {
                            ToastUtil.show(WordUtil.getString(R.string.edit_profile_right_date));
                        } else {
                            String result = DateFormat.format("yyyy-MM-dd", c).toString();
                            callback.onConfirmClick(result);
                            dialog.dismiss();
                        }
                    }
                } else {
                    dialog.dismiss();
                }
            }
        };
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(listener);
        dialog.show();
    }

    /**
     * 用户备注设置
     *
     * @return
     */
    public static Dialog showInputNick(Context context, String title, String content, final SimpleCallback callback) {

        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_input_nick);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
//        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        final AppCompatEditText etInput = dialog.findViewById(R.id.et_input);
        if (!TextUtils.isEmpty(content)) {
            etInput.setText(content);
        }
//        etInput.setFilters(new InputFilter[]{new SizeFilterTextAndLetter(), new InputFilter.LengthFilter(4)});
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etInput.getText().toString().trim();
                if (TextUtils.isEmpty(input)) {
                    ToastUtil.show(R.string.toast_input_nick);
                    return;
                }
                dialog.dismiss();
                callback.onConfirmClick(dialog, input);
            }
        });
        return dialog;
    }

    /**
     * 用户认证真实姓名、身份证确认
     *
     * @return
     */
    public static Dialog showTipsAuth(Context context, String content, final SimpleCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_tips_auth);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tvContent = dialog.findViewById(R.id.content);
        tvContent.setText(content);

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) callback.onConfirmClick(dialog, "");
            }
        });
        return dialog;
    }

    public static Dialog showCongelation(Context context, String content, SimpleCallback confirmCallback, SimpleCallback cancelCallback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_congelation_tips);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tvContent = dialog.findViewById(R.id.content);
        tvContent.setText(content);

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelCallback != null) {
                    cancelCallback.onConfirmClick(dialog, "");
                }
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmCallback != null) {
                    confirmCallback.onConfirmClick(dialog, "");
                }
            }
        });
        return dialog;
    }

    public static class Builder {

        private Context mContext;
        private String mTitle;
        private String mContent;
        private String mConfrimString;
        private String mCancelString;
        private boolean mCancelable;
        private boolean mBackgroundDimEnabled;//显示区域以外是否使用黑色半透明背景
        private boolean mInput;//是否是输入框的
        private String mHint;
        private int mInputType;
        private int mLength;
        private SimpleCallback mClickCallback;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setContent(String content) {
            mContent = content;
            return this;
        }

        public Builder setConfrimString(String confrimString) {
            mConfrimString = confrimString;
            return this;
        }

        public Builder setCancelString(String cancelString) {
            mCancelString = cancelString;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setBackgroundDimEnabled(boolean backgroundDimEnabled) {
            mBackgroundDimEnabled = backgroundDimEnabled;
            return this;
        }

        public Builder setInput(boolean input) {
            mInput = input;
            return this;
        }

        public Builder setHint(String hint) {
            mHint = hint;
            return this;
        }

        public Builder setInputType(int inputType) {
            mInputType = inputType;
            return this;
        }

        public Builder setLength(int length) {
            mLength = length;
            return this;
        }

        public Builder setClickCallback(SimpleCallback clickCallback) {
            mClickCallback = clickCallback;
            return this;
        }

        public Dialog build() {
            final Dialog dialog = new Dialog(mContext, mBackgroundDimEnabled ? R.style.dialog : R.style.dialog2);
            dialog.setContentView(mInput ? R.layout.dialog_input : R.layout.dialog_simple);
            dialog.setCancelable(mCancelable);
            dialog.setCanceledOnTouchOutside(mCancelable);
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            if (!TextUtils.isEmpty(mTitle)) {
                titleView.setText(mTitle);
            }
            final TextView content = (TextView) dialog.findViewById(R.id.content);
            if (!TextUtils.isEmpty(mHint)) {
                content.setHint(mHint);
            }
            if (!TextUtils.isEmpty(mContent)) {
                content.setText(mContent);
            }
            if (mInputType == INPUT_TYPE_NUMBER) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (mInputType == INPUT_TYPE_NUMBER_PASSWORD) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            } else if (mInputType == INPUT_TYPE_TEXT_PASSWORD) {
                content.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            if (mLength > 0 && content instanceof EditText) {
                content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLength)});
            }
            TextView btnConfirm = (TextView) dialog.findViewById(R.id.btn_confirm);
            if (!TextUtils.isEmpty(mConfrimString)) {
                btnConfirm.setText(mConfrimString);
            }
            TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
            if (!TextUtils.isEmpty(mCancelString)) {
                btnCancel.setText(mCancelString);
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_confirm) {
                        if (mClickCallback != null) {
                            if (mInput) {
                                mClickCallback.onConfirmClick(dialog, content.getText().toString());
                            } else {
                                dialog.dismiss();
                                mClickCallback.onConfirmClick(dialog, "");
                            }
                        } else {
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                        if (mClickCallback instanceof SimpleCallback2) {
                            ((SimpleCallback2) mClickCallback).onCancelClick();
                        }
                    }
                }
            };
            btnConfirm.setOnClickListener(listener);
            btnCancel.setOnClickListener(listener);
            return dialog;
        }

    }

    public interface DataPickerCallback {
        void onConfirmClick(String date);
    }

    public interface StringArrayDialogCallback {
        void onItemClick(String text, int tag);
    }

    public interface SimpleCallback {
        void onConfirmClick(Dialog dialog, String content);
    }

    public interface SimpleCallback2 extends SimpleCallback {
        void onCancelClick();
    }

}