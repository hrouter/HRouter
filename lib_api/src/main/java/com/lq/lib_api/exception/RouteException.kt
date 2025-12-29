package com.lq.lib_api.exception


open class RouteException(message: String): RuntimeException(message)

class GroupNotFoundException(group: String): RouteException("Group not found :$group")

class PathNotFoundException(path: String): RouteException("Path not found :$path")

class PathIllegalException(path: String): RouteException("Path : $path must start with / and at least 2 segments")

class ActivityNotFoundException(activity: String): RouteException("activity: $activity not found")

class ContextIllegalException(): RouteException("Context is required,Current Context is unknown ")

class RouteMetaIllegalException(): RouteException("Route Meta is not initialized")

class UriParseIllegalException(uri: String): RouteException("uri Parse Exception : $uri")

class RouteConflictException(path:String,classInfo:Class<*>,newClassInfo: Class<*>): RouteException("Route conflict : $path\n old:${classInfo} new:${newClassInfo}" )


class DegradeLoadException(className:String): RouteException("Expected List<IDegradeRegister> but found :${className}")