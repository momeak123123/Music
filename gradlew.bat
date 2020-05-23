/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.cyl.musiclake;
public interface IMusicService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.cyl.musiclake.IMusicService
{
private static final java.lang.String DESCRIPTOR = "com.cyl.musiclake.IMusicService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.cyl.musiclake.IMusicService interface,
 * generating a proxy if needed.
 */
public static com.cyl.musiclake.IMusicService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.cyl.musiclake.IMusicService))) {
return ((com.cyl.musiclake.IMusicService)iin);
}
return new com.cyl.musiclake.IMusicService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_nextPlay:
{
data.enforceInterface(descriptor);
com.cyl.musiclake.bean.Music _arg0;
if ((0!=data.readInt())) {
_arg0 = com.cyl.musiclake.bean.Music.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.nextPlay(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playMusic:
{
data.enforceInterface(descriptor);
com.cyl.musiclake.bean.Music _arg0;
if ((0!=data.readInt())) {
_arg0 = com.cyl.musiclake.bean.Music.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.playMusic(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playPlaylist:
{
data.enforceInterface(descriptor);
java.util.List<com.cyl.musiclake.bean.Music> _arg0;
_arg0 = data.createTypedArrayList(com.cyl.musiclake.bean.Music.CREATOR);
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
this.pl