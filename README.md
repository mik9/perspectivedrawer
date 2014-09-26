Perspective Drawer
=================
Example application: https://github.com/mik9/perspectivedrawerexample

<a href="http://i.imgur.com/GjBzqED.png"><img src="http://i.imgur.com/GjBzqEDl.png"></a>
<a href="http://i.imgur.com/b4yeCZY.png"><img src="http://i.imgur.com/b4yeCZYl.png"></a>

How to use
=================
Gradle:
```
compile 'ua.pl.mik:perspectivedrawer:0.2@aar'
```

Layout:
```
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
