echo
echo
echo "================== Starting Execution =================="
echo
echo "${TO}" > SHE.sh
. ./SHE.sh
[ $? -eq 0 ] && echo -e "\n================== Execution Success ==================" || echo -e "\n================== Execution Failed =================="
echo
