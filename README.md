# virtual-joystick-android


**v1.13.5** _(New version - [support custom images](#image), button & background size, limited direction, normalized coordinate, alpha border, Rectangle or circle support)_

_I created this very simple library as a learning process and I have been inspired by this project [JoystickView](https://github.com/zerokol/JoystickView) (the author is a genius!)_

This library provides a very simple and **ready-to-use** custom view which emulates a joystick for Android.

![Alt text](/misc/virtual-joystick-android.png?raw=true "Double Joystick with custom size and colors")

### Gist
Here is a very simple snippets to use it. Just set the `onMoveListener` to retrieve its angle and strength.

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ...

    JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
    joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
        @Override
        public void onMove(int angle, int strength, MotionEvent event) {
            // do whatever you want
        }
    });
}
```
The **angle** follow the rules of a simple **counter-clock** protractor. The **strength is percentage** of how far the button is **from the center to the border**.

![Alt text](/misc/virtual-joystick.png?raw=true "Explanation")


### Attributes

You can customize the joystick according to these attributes `JV_useRectangle`,`JV_axisToCenter`, `JV_buttonImage`, `JV_buttonColor`, `JV_buttonSizeRatio`, `JV_borderColor`, `JV_borderAlpha`, `JV_borderWidth`, `JV_backgroundColor`, `JV_backgroundSizeRatio`, `JV_fixedCenter`, `JV_autoReCenterButton`, `JV_buttonStickToBorder`, `JV_enabled` and `JV_buttonDirection`

If you specified `JV_buttonImage` you don't need `JV_buttonColor`

Here is an example for your layout resources:
```xml
<io.github.controlwear.virtual.joystick.android.JoystickView
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    custom:JV_buttonColor="#FF6E40"
    custom:JV_buttonSizeRatio="15%"
    custom:JV_borderColor="#00796B"
    custom:JV_backgroundColor="#009688"
    custom:JV_borderWidth="4dp"
    custom:JV_fixedCenter="false"/>
```
#### Image
If you want a more customized joystick, you can use `JV_buttonImage` and the regular `background` attributes to specify drawables. The images will be automatically resized.

```xml
<io.github.controlwear.virtual.joystick.android.JoystickView
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@drawable/joystick_base_blue"
    custom:JV_buttonImage="@drawable/ball_pink"/>
```

![Alt text](/misc/android-virtual-joystick-custom-image.png?raw=true "Left joystick with custom image")

#### Rectangle
It is possible to combine several configurations to create something new, such as a linear bar

![Alt text](/misc/virtual-joystick-android-rectangle.png?raw=true "rectangle joystick with vertical axis")

#### SizeRatio
We can change the default size of the button and background.
The size is calculated as a percentage of the total width/height.

By default, the button is 25% (25) and the background 75% (75), as the first screenshot above.

If the total (background + button) is above 100%, the button will probably be a bit cut when on the border.

```xml
<...
    custom:JV_buttonSizeRatio="50%"
    custom:JV_backgroundSizeRatio="10%"/>

```

```java
joystick.setBackgroundSizeRatio(50);
joystick.setButtonSizeRatio(10);
```

#### FixedCenter or Not? (and auto re-center)
If you don’t set up this parameter, it will be FixedCenter by default, which is the regular behavior.

However, sometimes, it is convenient to have an auto-defined center which will be defined each time you touch down the screen with your finger (center position will be limited inside the JoystickView’s width/height).
As every parameter you can set it up in xml (as above) or in Java:
```java
joystick.setFixedCenter(false); // set up auto-define center
```

UnfixedCenter (set to false) is particularly convenient when the user can’t (or doesn’t want to) see the screen (e.g. a drone's controller).

We can also remove the automatically re-centered button, just set it to false.
```java
joystick.setAutoReCenterButton(false);
```
_(The behavior is a bit weird if we set remove both the FixedCenter and the AutoReCenter.)_

#### Enabled
By default the joystick is enabled (set to True), but you can disable it either in xml or Java. Then, the button will stop moving and `onMove()` won’t be called anymore.
```java
joystick.setEnabled(false); // disabled the joystick
joystick.isEnabled(); // return enabled state
```

#### AxisMotion
By default the button can move in both direction X,Y (regular behavior), but we can limit the movement through one axe horizontal or vertical.
```xml
<...
    custom:JV_axisMotion="horizontal"/>
```
In the layout file (xml), this option can be set to `horizontal`, `vertical` or `both`.

We can also set this option in the Java file by setting an integer value:
- `BUTTON_DIRECTION_HORIZONTAL` for the horizontal axe
- `BUTTON_DIRECTION_VERTICAL` for the vertical axe
- `BUTTON_DIRECTION_BOTH` for both (which is the default option)

```java
joystick.setAxisMotion(JoystickView.BUTTON_DIRECTION_VERTICAL); // vertical
```

#### Position
The joystick is divided into eight positions + `none`

![Alt text](/misc/virtual-joystick-positions.png?raw=true " stick positions")

```java
joystick.getPosition() == JoystickView.RIGHT_UP; 
```

#### Deadzone
Most joysticks have an inner deadzone, else games would feel hypersensitive in their inputs.
By default, the deadzone is at 10% strength.
To change the deadzone, you can specify the following in xml.
```xml
<...
    custom:JV_deadzone="10%"/>
```

Or better, if you just want a simple Joystick (and few other cool stuff) as a controller for your mobile app you can use the following related project ;)

## Demo
For those who want more than just a snippet, here is the demo :
- [Basic two joysticks ](https://github.com/lukkass222/virtual-joystick-android/tree/master/joystickdemo_2) [similar to screenshot](#image).
- [Basic joystick ](https://github.com/lukkass222/virtual-joystick-android/tree/master/joystickdemo) [similar to screenshot](#Rectangle).


## Required
Minimum API level is 16 (Android 4.1.x - Jelly Bean) which cover 99.5% of the Android platforms as of October 2018 according to the  <a href="https://developer.android.com/about/dashboards" class="user-mention">distribution dashboard</a>.

## Download
### Gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```Gradle
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.** Add the dependency

```Gradle
dependencies {
	        implementation 'com.github.lukkass222:virtual-joystick-android:1.13.5'
	}
```

## Contributing
If you would like to contribute code, you can do so through GitHub by forking the repository and sending a pull request.
When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible.

## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Authors

**virtual-joystick-android** is an open source project created by <a href="https://github.com/makowildcat" class="user-mention">@makowildcat</a> (mostly spare time) and partially funded by [Black Artick](http://blackartick.com/) and [NSERC](http://www.nserc-crsng.gc.ca/index_eng.asp).

Also, thanks to <a href="https://github.com/Bernix01" class="user-mention">Bernix01</a>,
<a href="https://github.com/teancake" class="user-mention">teancake</a>,
<a href="https://github.com/Spettacolo83" class="user-mention">Spettacolo83</a>,
<a href="https://github.com/djjaysmith" class="user-mention">djjaysmith</a>,
<a href="https://github.com/jaybkim1" class="user-mention">jaybkim1</a>,
<a href="https://github.com/sikrinick" class="user-mention">sikrinick</a>,
<a href="https://github.com/AlexandrDavydov" class="user-mention">AlexandrDavydov</a>,
<a href="https://github.com/indrek-koue" class="user-mention">indrek-koue</a>,
<a href="https://github.com/QitmentX7" class="user-mention">QitmentX7</a>,
<a href="https://github.com/esplemea" class="user-mention">esplemea</a>,
<a href="https://github.com/FenixGit" class="user-mention">FenixGit</a>,
<a href="https://github.com/AlexanderShniperson" class="user-mention">AlexanderShniperson</a>
, <a href="https://github.com/omarhemaia" class="user-mention">omarhemaia</a>,
<a href="https://github.com/mstniy" class="user-mention">mstniy</a>
<a href="https://github.com/Mathias-Boulay" class="user-mention">Mathias-Boulay</a>,
<a href="https://github.com/osfunapps" class="user-mention">osfunapps</a>,
<a href="https://github.com/eziosoft" class="user-mention">eziosoft</a>,
<a href="https://github.com/scottbarnesg" class="user-mention">scottbarnesg</a>,
<a href="https://github.com/tomerlevi444" class="user-mention">tomerlevi444</a>,
<a href="https://github.com/BenDelGreco" class="user-mention">BenDelGreco</a>,
<a href="https://github.com/dooully" class="user-mention">dooully</a>,
<a href="https://github.com/jonyhunter" class="user-mention">jonyhunter</a>,
<a href="https://github.com/lukkass222" class="user-mention">lukkass222</a> 
and <a href="https://github.com/GijsGoudzwaard" class="user-mention">GijsGoudzwaard</a> for contributing.
