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
- `<$name>` - inject the value of name that was passed to the template
- `<$if object>` - only include the following template if object exists OR object is true if object is a boolean (the type is checked so the string "false" is considered to be true)
- `<$elseif condition>` - include the following block if condition is true OR exists and no prior if or elseif blocks was included
- `<$else>` - include the following block if no prior if or elseif block was included
- `<$end if>` - closes the most recently opened if block 
- `<$each list as item>` - loop over the following template for each item in a list do nothing if data.list doesn't exist (throws exception if parameter isn't a list or if name given isn't unique in scope)
- `<$each map as key value>` - loop over following template for each key value pair in a map do nothing if data.map doesn't exist (throws exception if parameter isn't a map or if names given aren't unique in scope)
- `<$end each>` - closes the most recently opened each block
- `<$include 'templateName' arg>` - include the following template and pass arg as the data model (arg is optional, if it isn't present then data is an empty object)

## Syntax

Template files are regular text files with Weasel Template markup wrapped in `<$>`'s.

```html
<$include 'header.wt' user>

<$if user.isAdmin>
  <$include 'admin.wt' user>
<$elseif user.isRegularUser>
  Hello <$user.name>
<$else>
  <$include 'login.wt'>
<$end if>

<ul>
<$each insults as insult>
  <li><$user.name> you are <$insult></li>
<$end each>
</ul>
```

## Kotlin API

```kotlin
val engine = WeaselTemplateEngine() //cache is only in use if engine instance is reused
val data = //create JSON object
val lines = //read in or supply a Stream<String>
val result = engine.process('templatename.wt', lines, data)
```
