handwritten
===========

UQ COMP7702 AI-A3

`push` 
`~/.gitconfig`

[Git Completion](http://blog.yxwang.me/2010/01/git-completion/)
[git-completion.bash](http://repo.or.cz/w/git.git/blob_plain/HEAD:/contrib/completion/git-completion.bash)


[How startup ideas grow](http://paulgraham.com/startupideas.html)

```python
import timeit

timeit.timeit(stmt="pass", setup="pass", timer=<default>, number=1000000)
# or
timeit.Timer(stmt="pass", setup="pass", timer=<default>).timeit(number=1000000)
```

```python
$ git config --system # access file in the system directory
$ git config --global # access file in the home directory
# override in individual repo
$ cd <project directory>
$ git config user.name ""
```
