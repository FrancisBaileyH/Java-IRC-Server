package com.francisbailey.irc;

/**
 * Hierarchy?
 *
 * Server
 *    -> Channel
 *          -> User
 *
 * Server
 *    -> User
 *
 *
 * Created by fbailey on 12/07/17.
 */
public class ModeContext {

    private ModeTarget target;
    private ModeResource resource;


    public ModeContext(ModeTarget target, ModeResource resource) {
        this.target = target;
        this.resource = resource;
    }

    public String getContextName() {

        return ModeContext.getContextName(this.target, this.resource);
    }


    public static String getContextName(ModeTarget target, ModeResource resource) {

        return resource.getResourceType() + "-" + target.getTargetType();
    }


    public boolean equals(ModeContext context) {

        return this.getContextName().equals(context.getContextName());
    }

}
