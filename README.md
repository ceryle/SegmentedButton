[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SegmentedButton-green.svg?style=true)](https://android-arsenal.com/details/1/4445)
# SegmentedButton

![poster](https://cloud.githubusercontent.com/assets/20969019/19036424/75a0b61c-8978-11e6-873d-e316f77fd740.png)


Segmented Button is a IOS-like "Segmented Control" with animation.<br/>
For more Android-like segmented control, check [Radio Real Button](https://github.com/ceryle/RadioRealButton).

##Preview
![1](https://cloud.githubusercontent.com/assets/20969019/19036452/9abd66e8-8978-11e6-84f3-2942ec2feb6d.gif)
<br />
![2](https://cloud.githubusercontent.com/assets/20969019/19036451/9abab79a-8978-11e6-8594-8590e95f7b03.gif)
<br />
![3](https://cloud.githubusercontent.com/assets/20969019/19036454/9ac12026-8978-11e6-9c4c-148996f080b7.gif)
![4](https://cloud.githubusercontent.com/assets/20969019/19036455/9ac1c6f2-8978-11e6-91df-efcb5cdee110.gif)
<br />
![5](https://cloud.githubusercontent.com/assets/20969019/19036453/9ac057c2-8978-11e6-9ecb-a72ca25b39cf.gif)
<br />
![6](https://cloud.githubusercontent.com/assets/20969019/19036456/9ac4a7c8-8978-11e6-9a1c-8cf1bb933026.gif)


## Installation

#### Gradle

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    compile 'com.github.ceryle:SegmentedButton:v1.1.7'
}
```

## Customization

### Some Attributes

#### Segmented Button
| Option Name      				| Format                 | Description                              |
| ---------------- 				| ---------------------- | -----------------------------            |
| app:sb_imageTint         | `color`               |  Set tint onto button's image    |
| app:sb_imageScale       | `float`               | Scale button's image |
| app:sb_selectedImageTint 		| `color`		         | Set tint onto button's image if selector on it  |
| app:sb_selectedTextColor     | `color`               | Set color onto button's text if selector on it      |
| app:sb_rippleColor        | `color`               | Set ripple color of button  |

#### Segmented Button Group
| Option Name      				| Format                 | Description                              |
| ---------------- 				| ---------------------- | -----------------------------            |
| app:sbg_ripple         | `boolean`               |  Set ripple color for every button    |
| app:sbg_rippleColor         | `color`               |  Set ripple color for every button with custom color    |
| app:sbg_selectorImageTint       | `color`               | If selector on it, set tint onto image for every button  |
| app:sbg_selectorTextColor 		| `color`		         | If selector on it, set text color for every button  |
| app:sbg_selectorColor     | `color`               | Set selector color  |
| app:sbg_dividerSize        | `dimension`               | Set divider size  |
| app:sbg_dividerPadding    		| `dimension`           | Set divider padding for top and bottom  |
| app:sbg_dividerColor			| `color`	         | Change divider color |
| app:sbg_dividerRadius			| `dimension`	         | Round divider |
| app:sbg_shadow			| `boolean`	         | Shadow for container layout (api21+) |
| app:sbg_shadowElevation			| `dimension`	         | Shadow for container layout (api21+) |
| app:sbg_shadowMargin			| `dimension`	         | Set margin to make shadow visible (api21+) |
| app:sbg_position			| `integer`	         | Set selected button position |
| app:sbg_radius			| `dimension`	         | Make layout rounder |
| app:sbg_backgroundColor			| `color`	         | Set background color of container |
| app:sbg_animateSelectorDuration			| `integer`	         | Set how long selector travels to selected position |
| app:sbg_animateSelector			| `integer`	         | Set selector animation (ex. bounce animation) |
| app:sbg_borderSize			| `dimension`	         | Add border by giving dimension |
| app:sbg_borderColor			| `color`	         | Change border color (Default: Grey) |

#### Examples

##### In Xml Layout

```xml
    <co.ceryle.segmentedbutton.SegmentedButtonGroup
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        app:sbg_animateSelector="bounce"
        app:sbg_animateSelectorDuration="1000"
        app:sbg_backgroundColor="@color/white"
        app:sbg_dividerColor="@color/grey_500"
        app:sbg_dividerPadding="10dp"
        app:sbg_dividerRadius="10dp"
        app:sbg_dividerSize="1dp"
        app:sbg_position="1"
        app:sbg_radius="2dp"
        app:sbg_ripple="true"
        app:sbg_rippleColor="@color/grey_500"
        app:sbg_selectorColor="@color/grey_500"
        app:sbg_selectorTextColor="@color/white"
        app:sbg_shadow="true"
        app:sbg_shadowElevation="3dp"
        app:sbg_shadowMargin="4dp">

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:minHeight="10dp"
            android:text="Button 1"
            android:textAllCaps="false" />

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:minHeight="10dp"
            android:text="Button 2"
            android:textAllCaps="false" />

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:minHeight="10dp"
            android:text="Button 3"
            android:textAllCaps="false" />

    </co.ceryle.segmentedbutton.SegmentedButtonGroup>
```

##### Listener Example
```java
SegmentedButtonGroup segmentedButtonGroup = (SegmentedButtonGroup) findViewById(R.id.segmentedButtonGroup);
segmentedButtonGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
    @Override
    public void onClickedButtonPosition(int position) {
        Toast.makeText(MainActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
    }
});
segmentedButtonGroup.setPosition(2, 0);
```

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details

