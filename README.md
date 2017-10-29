Weasel Template
===============

*This project is still under development.*

A minimalist templating engine for the JVM based on JSON &amp; implemented in Kotlin.

## Features
- Use JSON for datamodel (via GSON)
- Target HTML output
- Play well with OSGi
- Support a minimal number of features

## Commands
- `<$data.name>` - inject the value of name that was passed to the template
- `<$if data.object>` - only include the following template if object exists OR object is true if object is a boolean (the type is checked so the string "false" is considered to be true)
- `<$each data.list as item>` - loop over the following template for each item in a list do nothing if data.list doesn't exist (throws exception if parameter isn't a list or if name given isn't unique in scope)
- `<$each data.map as key value>` - loop over following template for each key value pair in a map do nothing if data.map doesn't exist (throws exception if parameter isn't a map or if names given aren't unique in scope)
- `<$include 'templateName' arg>` - include the following template and pass arg as the data model (arg is optional, if it isn't present then data is an empty object)

## Syntax

Template files are regular text files with Weasel Template markup wrapped in `<$>`'s.

```html
<$include 'header.wt' data>

<$if data.isAdmin>
  <$include 'admin.wt' data>
<$elseif data.isRegularUser>
  Hello <$data.name>
<$else>
  <$include 'login.wt' data>
<$end if>

<ul>
<$each data.insults as insult>
  <li><$data.name> you are <$insult></li>
<$end each>
</ul>
```

## Kotlin API

```kotlin
val engine = WeaselTemplateEngine(this.class) //you can/probably should reuse this
val data = //create JSON object
val result = engine.process('templatename.wt', data)
```