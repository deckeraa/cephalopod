# cephalopod

cephalopod is static site generator using Clojure.
Blog posts take the form of s-expressions stored in their own file.
Blog posts are executed when the site compiles; consider this and the
security implications when you use cephalopod.

## Design & Justification
The overaching design goal is simplicity.

### Static site generation
To host my blog on GitHub, I need to be able to statically host the site.
Also, static generation works into the goal of simplicity by putting the blog into the
standard paradigm of file hierarchy.

### Content Storage
No database. A database requires configuration and is unnecessary for this application.
Rather, posts will be kept in a flat folder inside the project directory.
Since I plan to handle posts based on title rather than date,
it makes more sense to not have to mess with navigating a three-deep folder structure
with posts sorted by date.

### Posts stored as s-expressions

<!-- Blog posts are stored as s-expressions: -->
<!-- (defpost {:title "blog-post-title" :date date}  -->


## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2014 Aaron Decker
