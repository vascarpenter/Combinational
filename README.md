# SD prompts Combinational Reorderer
# This program converts clipboard.

```
# header comment
# header comment continues until # + number comment lines
# 1 .. series 1
some prompt
# 2 .. series 2
another prompt
# 3 .. series 3
just another prompt
```
# to  2, 1, 3 order
```
# header comment
# header comment continues until # + number comment lines
# 2 .. series 2
another prompt
# 1 .. series 1
some prompt
# 3 .. series 3
just another prompt
```
# reset button reorders prompts as #+num specifies order

# why you make this?
- Stable diffusion wildcard extension "Dynamic Prompts - Combinatorial generation" will extract all prompts line by line, but it's not sequential order..
- for example
# つーかプロンプト英語順だったわ　prompt先頭に01とかつけとけば大丈夫じゃね
