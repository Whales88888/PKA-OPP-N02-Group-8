
#!/bin/bash

case "$1" in
  bvk)
    git config user.name "bvk04042005"
    git config user.email "bvk04042005@gmail.com"
    echo "✅ Đã chuyển sang user: bvk"
    ;;
  whales)
    git config user.name "Whales88888"
    git config user.email "whales88888@example.com"
    echo "✅ Đã chuyển sang user: whales"
    ;;
  mine)
    git config user.name "minemiagobnn"
    git config user.email "minemiagobnn@example.com"
    echo "✅ Đã chuyển sang user: mine"
    ;;
  current)
    echo "📌 User hiện tại:"
    echo "Name : $(git config user.name)"
    echo "Email: $(git config user.email)"
    ;;
  *)
    echo "❌ Cú pháp sai! Dùng: ./gituse.sh [bvk|whales|mine|current]"
    ;;
esac

