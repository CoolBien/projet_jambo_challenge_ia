import check_solution as cs

def test_ObjectType():
	print("-- test_ObjectType")
	#test new
	o = cs.ObjectType(22,5,"type d'objet utile",344,1222)
	assert(o.id==22)
	assert(o.NB==5)
	assert(o.name=="type d'objet utile")
	assert(o.height==344)
	assert(o.width==1222)
	print("   "+str(o))
	#test get_new_num()
	for _ in range(7):
		try:
			num = o.get_new_num()
			print("ok new num = "+str(num))
		except Exception:
			print("ok (get_new_num() error detected)")
		else:
			assert(num>=0 and num<=4)
	#print("  num = "+str(num))
	#test reset_num()
	o.reset_num()
	assert(o.get_new_num()==0)
	print("-- ok")


def test_InputData(data_file_name):
	print("-- test_InputData")
	inputd = cs.InputData()
	#test no existing file:
	try:
		inputd.read_json_file(data_file_name)
	except FileNotFoundError:
		print("ok (read_json_file() error detected): '",data_file_name,"' not found")
		return None

	print("   ",inputd)

	it0 = inputd.find_item_type(0)
	assert(it0!=None)
	it1 = inputd.find_item_type(1)
	assert(it1!=None)
	it2 = inputd.find_item_type(2)
	assert(it2==None)
	print("ok find_item_type()")
	
	jt0 = inputd.find_jumbo_type(0)
	assert(jt0!=None)
	jt1 = inputd.find_jumbo_type(1)
	assert(jt1!=None)
	jt2 = inputd.find_jumbo_type(2)
	assert(jt2==None)
	print("ok find_jumbo_type()")
	print("-- ok")
	return inputd

def test_ProblemData(input_data):
	print("-- test_ProblemData")
	pdata = cs.ProblemData(input_data)
	#check total number of items to place
	assert(len(pdata.item_list)==7)
	#check total number of kumbos to use
	assert(len(pdata.jumbo_list)==30)
	print("   ",pdata)
	#check known items
	i0 = pdata.find_item(0,0)
	assert(i0!=None)
	i1 = pdata.find_item(1,0)
	assert(i1!=None)
	i2 = pdata.find_item(0,1)
	assert(i2!=None)
	#check unknown items
	i3 = pdata.find_item(0,4)
	assert(i3==None)
	i4 = pdata.find_item(1,3)
	assert(i4==None)
	print("ok find_item()")
	
	j0 = pdata.find_jumbo(0,0)
	assert(j0!=None)
	j1 = pdata.find_jumbo(0,14)
	assert(j1!=None)
	j2 = pdata.find_jumbo(0,15)
	assert(j2==None)
	print("ok find_jumbo()")

	#check all new_num internal variables are 0
	for it in input_data.item_type_list:
		assert(it._next_new_num==0)

	for jt in input_data.jumbo_type_list:
		assert(jt._next_new_num==0)

	print("-- ok")
	return pdata


def test_Solution(input_data,problem_data):
	print("-- test_Solution")
	sol = cs.Solution(input_data,problem_data)
	#check error in filne name
	try:
		sol.read_json_file("no_exist.json")
	except FileNotFoundError:
		print("ok (read_json_file() error detected)")
	#check error in file number of items used 
	try:
		sol.read_json_file("test_files/output_test_false.json")
	except Exception:
		print("ok read_json_file() item nb error detected")
		for it in input_data.item_type_list:
			it.reset_num()

		for jt in input_data.jumbo_type_list:
			jt.reset_num()

	sol.read_json_file("test_files/output_test.json")
	print("   solution (before check)=",sol)
	sol.check()
	print("   solution (after check)=",sol)

	sol.save_svg("sol_test.svg")

	print("-- ok")
	return sol

if __name__=="__main__":
	print("--------------------\nT E S T S\n--------------------")
	test_ObjectType()
	input_data = test_InputData("no_exist.json")
	input_data = test_InputData("test_files/input_test.json")
	problem_data = test_ProblemData(input_data)
	solution = test_Solution(input_data,problem_data)
	