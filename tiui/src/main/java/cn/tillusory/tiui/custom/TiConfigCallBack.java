package cn.tillusory.tiui.custom;

public interface TiConfigCallBack<T> {

  void success(T list);

  void fail(Exception error);

}
