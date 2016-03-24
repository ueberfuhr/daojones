src/site allows the following file formats, but are not used because...
(.vm chains the file to velocity at first, so ${...} expressions are resolved as maven properties)

full documentation see http://maven.apache.org/doxia/references/

/apt/*.apt			- APT (Almost Plain Text)		- does not support image links, html inline styles, html style attributes
/apt/*.apt.vm										+ simple table syntax

/xdoc/*.xml			- XDoc							- large XML files, *.vm cannot be opened with the XML editor within Eclipse
/xdoc/*.xml.vm

/fml/*.fml			- FAQ Markup Language
/fml/*.fml.vm

/markdown/*.md		- MarkDown						+ embedded HTML allowed
/markdown/*.md.vm									- sub sections do not work (####) with .vm -> use <h4>