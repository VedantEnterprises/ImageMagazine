package xyz.hanks.imagemagazine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hanks on 16/4/2.
 */
public class SubscribeModel implements Parcelable {
    public int id;
    public String name;
    public int valid;
    public String supplyDesc;
    public String description;
    public int factor;
    public int catId;
    public String img;
    public int totalSubscribeNum;
    public long createTimeInMills;
    public long updateTimeInMills;

    protected SubscribeModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        valid = in.readInt();
        supplyDesc = in.readString();
        description = in.readString();
        factor = in.readInt();
        catId = in.readInt();
        img = in.readString();
        totalSubscribeNum = in.readInt();
        createTimeInMills = in.readLong();
        updateTimeInMills = in.readLong();
    }

    public static final Creator<SubscribeModel> CREATOR = new Creator<SubscribeModel>() {
        @Override
        public SubscribeModel createFromParcel(Parcel in) {
            return new SubscribeModel(in);
        }

        @Override
        public SubscribeModel[] newArray(int size) {
            return new SubscribeModel[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(valid);
        dest.writeString(supplyDesc);
        dest.writeString(description);
        dest.writeInt(factor);
        dest.writeInt(catId);
        dest.writeString(img);
        dest.writeInt(totalSubscribeNum);
        dest.writeLong(createTimeInMills);
        dest.writeLong(updateTimeInMills);
    }
}
