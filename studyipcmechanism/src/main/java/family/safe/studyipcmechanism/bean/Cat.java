package family.safe.studyipcmechanism.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 实现Parcelable接口 序列化,
 * 优点 : 效率高  缺点 : 使用麻烦
 * 内存序列化优选
 * Created by Administrator on 2016/8/2.
 */
public class Cat implements Parcelable {
    public String name;
    public int age;
    public Person person;

    public Cat(int age, String name, Person person) {
        this.age = age;
        this.name = name;
        this.person = person;
    }


    public static final Creator<Cat> CREATOR = new Creator<Cat>() {
        @Override
        public Cat createFromParcel(Parcel in) {
            return new Cat(in);
        }

        @Override
        public Cat[] newArray(int size) {
            return new Cat[size];
        }
    };

    @Override
    public int describeContents() {
        /**
         * 如果含有文件描述,返回1,否则返回0
         * 几乎所有情况都返回0
         */
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        /**
         * 序列化操作
         */
        out.writeString(name);
        out.writeInt(age);
        out.writeSerializable(person);
    }

    protected Cat(Parcel in) {
        /**
         * 反序列化操作
         */
        age = in.readInt();
        name = in.readString();
        person = (Person) in.readSerializable();
    }
}
