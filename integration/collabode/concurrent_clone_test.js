var attribs = appjet.context.attributes();

var username = attribs.get("test-username").get();
utils.getSession().userId = "r." + username;
utils.getSession().userName = username;

var project = attribs.get("test-project").get();

auth.add_acl(project, "", "anyone", "claim");

clone_control.clone_path(project.getName(), "src/Hello.java");

// clone_path stops with redirect
