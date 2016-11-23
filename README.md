# Guvercin (Pigeon)
Annotation processing library for LocalBroadcastManager. Allows you to define LocalBroadcastManager callback methods easily on your Activity or Fragments.

## Sample Usage

In your Activity or Fragment you can setup LocalBroadcastManager callbacks using the `@Guvercin` annotation:

```
@Guvercin("Broadcast_Tag")
public void myCallback(){
  // Code the execute when "Broadcast_Tag" is broadcast
}
```

or if you need to access the Intent:

```
@Guvercin("Broadcast_Tag")
public void myCallback(Intent intent){
  // You can access get data stored inside the intent
}
```

Make sure you initialize and destory the Guvercin library in your base Activity or Fragment:
```
public abstract class BaseActivity extends AppCompatActivity {

    private GuvercinUnbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = GuvercinManager.init(this);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}

```

You don't need to change how to call the LocalBroadcastManager to send the messages:

```
LocalBroadcastManager.getInstance(this)
                     .sendBroadcast(new Intent("Broadcast_Tag"));
```

Please see the sample project (app module) to see how it's used in a project.

## Download

```
compile 'me.jaxvy.guvercin:guvercin:1.0.0'
compile 'me.jaxvy.guvercin:guvercin-annotations:1.0.0'
apt 'me.jaxvy.guvercin:guvercin-compiler:1.0.0'
```

## License

```
The MIT License

Copyright (c) 2016 Jaxvy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

