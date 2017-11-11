Weasel Template
===============

*This project is still under development and isn't in a usable state yet.*

Weasel Template is a minimalist template engine for the JVM based on JSON &amp; implemented in Kotlin.
Every other JVM templating library contains a very large number of features that aren't needed for
very simple use cases like handling the initial server side rendering of webpages that use client side
javascript for most user interaction.  This is the use case that Weasel Template was created to
handle.  If you need more features than what Weasel Template provides there are over a dozen
much more feature laden templating libraries that exist for the JVM.

## Features
- Use JSON for data model (via GSON)
- Target HTML output
- Play well with OSGi
- Support a minimal number of features (conditionals, list iteration, and includes)
- Example based testing via Spock

## Tags
- `<?name>` - inject the value of name that was passed to the template
- `<?if object>` - only include the following template if object exists OR object is true if object is a boolean (the type is checked so the string "false" is considered to be true)
- `<?elseif condition>` - include the following block if condition is true OR exists and no prior if or elseif blocks was included
- `<?else>` - include the following block if no prior if or elseif block was included
- `<?end if>` - closes the most recently opened if block 
- `<?each list as item>` - loop over the following template for each item in a list do nothing if data.list doesn't exist (throws exception if parameter isn't a list or if name given isn't unique in scope)
- `<?end each>` - closes the most recently opened each block
- `<?include 'templateName' arg>` - include the following template and pass arg as the data model (arg is optional, if it isn't present then data is an empty object)

## Example Template

Template files are regular text files with Weasel Template markup wrapped in angle brackets that start with a single ?.

```html
<?include 'header.wt' user>

<?if user.isAdmin>
  <?include 'admin.wt' user>
<?elseif user.isRegularUser>
  Hello <?user.name>
<?else>
  <?include 'login.wt'>
<?end if>

<ul>
<?each insults as insult>
  <li><?user.name> you <?insult>!</li>
<?end each>
</ul>
```

Below is a sample JSON document that could be used with the above template.

```json
{
  "user" : {
    "isAdmin" : true,
    "isRegularUser" : false,
    "name" : "Pat"
  }
  "insults" : ["are a hamster", "smell of elderberries"]
}
```

## Kotlin API

The following code example demonstrates the two public methods and single constructor that
Weasel Template supplies.  All template files are loaded from the class loader passed into
WeaselTemplateEngine's constructor.  Types are only there for the sake of documentation.

```kotlin
val engine = WeaselTemplateEngine(this.javaClass.classLoader) //cache is only in use if engine instance is reused
val data: JsonObject = createJsonObject()
val result: String = engine.processTemplate('templatename.wt', data)
engine.clearCache() //delete cached templates, you probably won't need to call this often/ever
```
