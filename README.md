Perspective Drawer
=================
Checkout the video on [youtube](https://www.youtube.com/watch?v=-LcN3qqp3oA)

<a href="http://i.imgur.com/GjBzqED.png"><img src="http://i.imgur.com/GjBzqEDl.png"></a>
<a href="http://i.imgur.com/b4yeCZY.png"><img src="http://i.imgur.com/b4yeCZYl.png"></a>

How to use
=================
Gradle:
```groovy
dependencies {
    compile('ua.pl.mik:perspectivedrawer:0.3.2@aar') {
        transitive = true
    }
}
```

Layout:
```xml
<ua.pl.mik.perspectivedrawer.PerspectiveDrawer
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/drawer"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Menu -->
    <ua.pl.mik.perspectivedrawer.PageHolder
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- Your views here -->
    </ua.pl.mik.perspectivedrawer.PageHolder>

    <!-- Main page -->
    <ua.pl.mik.perspectivedrawer.PageHolder
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/perspective_drawer_shadow">
        <!-- Your views here -->
    </ua.pl.mik.perspectivedrawer.PageHolder>

</ua.pl.mik.perspectivedrawer.PerspectiveDrawer>
```

Control:
```java
// Get reference to Perspective Drawer from the layout
PerspectiveDrawer perspectiveDrawer = (PerspectiveDrawer) findViewById(R.id.drawer);

// To open on an event , call
perspectiveDrawer.open();

// To close on an event , call
perspectiveDrawer.close();
```

Google's DrawerLayout compatibility
=================
As for v0.3 compatibility was taken into consideration 
+ `ua.pl.mik.perspectivedrawer.DrawerLayout` class was added which provides same methods as 
Google's one. 
+ `ActionBarDrawerToggle` was implemented so you can use it with default actionbar.

Migrating between this two drawers is easy and takes up to 20 min!
