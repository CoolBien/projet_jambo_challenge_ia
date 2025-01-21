#check_solution.py
#check a solution for the cutting problem


import json
import svg
from textwrap import dedent
import sys

class ObjectType:
    def __init__(self,id, nb, name, height, width):
        self.id = id
        self.NB = nb #constant
        self.name = name
        self.height = height
        self.width = width
        self._next_new_num = 0 #used to count item of that type

    def __str__(self):
        return ("id="+str(self.id)+" NB="+str(self.NB)+" \""
               +self.name+"\" "+" h="+str(self.height)+" w="+str(self.width))

    def get_new_num(self):
        num = -1
        if self._next_new_num>=self.NB:
            raise Exception("ObjectType Error","impossible to provide a num>=" +str(self.NB)+" for type \'"+str(self.id)+"\'\n")
        else:
            num = self._next_new_num
            self._next_new_num += 1
        return num

    def reset_num(self):
        self._next_new_num = 0

class ItemType(ObjectType):
    def __init__(self,id=0, nb=0, name="unknown", height=0, width=0):
        super().__init__(id,nb,name,height,width)
        
    def __str__(self):
        return ("item type:"+super().__str__())

class JumboType(ObjectType):
    def __init__(self,id=0, nb=0, name="unknown", height=0, width=0):
        super().__init__(id,nb,name,height,width)
        
    def __str__(self):
        return ("jumbo type:"+super().__str__())

class InputData:
    def __init__(self,input_file_name="cut_data.json"):
        self.input_file_name = input_file_name
        self.item_type_list = []
        self.jumbo_type_list = []
        self.cut_width = 0
        #usefull to check valid solution only
        self._item_type_list_id = None
        self._jumbo_type_list_id = None
        
    def find_item_type(self,id):
        if self._item_type_list_id==None:
            self._item_type_list_id = [item.id for item in self.item_type_list]
        try:
            index = self._item_type_list_id.index(id)
        except ValueError:
            return None
        else:
            return self.item_type_list[index]
        
    def find_jumbo_type(self,id):
        if self._jumbo_type_list_id==None:
            self._jumbo_type_list_id = [jumbo.id for jumbo in self.jumbo_type_list]
        try:
            index = self._jumbo_type_list_id.index(id)
        except ValueError:
            return None
        else:
            return self.jumbo_type_list[index]
        
    def __str__(self):
        return "input data:item type list="+str([str(i) for i in self.item_type_list])+" jumbo list="+str([str(j) for j in self.jumbo_type_list])+" cut width="+str(self.cut_width)
    
    def read_json_file(self,file_name=None):
        if file_name==None:
            file_name = self.input_file_name
        else:
            self.input_file_name = file_name
        try:
            json_text = ""
            with open(file_name,"r") as f_in:
                json_lines = f_in.readlines()
            for lines in json_lines:
                json_text += lines.replace('\n','')
        except FileNotFoundError as msg:
            raise #call other exception handler before exit()
            print("Error: "+str(msg))
            exit()
        input_json = json.loads(json_text)
        for item in input_json["items"]:
            #print(item)
            new_item_type = ItemType(item["id"],
                            int(item["nb"]),
                            item["name"],
                            int(item["size"]["height"]),
                            int(item["size"]["width"]))
            self.item_type_list.append(new_item_type)
        for jumbo in input_json["jumbos"]:
            new_jumbo_type = JumboType(jumbo["id"],
                              int(jumbo["nb"]),
                              jumbo["name"],
                              int(jumbo["size"]["height"]),
                              int(jumbo["size"]["width"]))
            self.jumbo_type_list.append(new_jumbo_type)
            try:
                self.cut_width = int(input_json["cut-width"])
            except KeyError:
                #default value = 0
                self.cut_width = 0
 
class Item:
    def __init__(self,item_type):
        self.type = item_type
        self.num = item_type.get_new_num()
        self.jumbo_sub_part = None
        
    def __str__(self):
        if self.jumbo_sub_part == None:
            return ("item id="+str(self.type.id)+"#"+str(self.num)+"/"+str(self.type.NB)+ " not placed")
        else:
            return ("item id="+str(self.type.id)+"#"+str(self.num)+"/"+str(self.type.NB)+" (x1,y1)="+str(self.x1)+","+str(self.y1)+" (x2,y2)="+str(self.x2)+","+str(self.y2))

class Jumbo:
    def __init__(self,jumbo_type,num=0):
        self.type = jumbo_type
        self.num = jumbo_type.get_new_num()

    def __str__(self):
        return ("jumbo id="+str(self.type.id)+"#"+str(self.num)+"/"+str(self.type.NB))
    
class ProblemData:
    def __init__(self,input_data):
        #create list of items to deal with
        self.item_list = []
        for item_type in input_data.item_type_list:
            for _ in range(item_type.NB):
                new_item = Item(item_type)
                self.item_list.append(new_item)
            item_type.reset_num()
        #create list of jumbos to deal with
        self.jumbo_list = []
        for jumbo_type in input_data.jumbo_type_list:
            for _ in range(jumbo_type.NB):
                new_jumbo = Jumbo(jumbo_type)
                self.jumbo_list.append(new_jumbo)
            jumbo_type.reset_num()
        #List of id+num to find items and jumbos with their id+num
        #they are built in find_* functions
        self._item_list_id_num = None
        self._jumbo_list_id_num = None

    
    def __str__(self):
        return "problem data:item list="+str([str(i) for i in self.item_list])+" jumbo list=\n"+str([str(j) for j in self.jumbo_list])
    
    def find_item(self,id,num):
        if self._item_list_id_num==None:
            self._item_list_id_num = [(item.type.id,item.num) for item in self.item_list]
        try:
            index = self._item_list_id_num.index((id,num))
        except ValueError:
            return None
        else:
            return self.item_list[index]
   
    def find_jumbo(self,id,num):
        if self._jumbo_list_id_num==None:
            self._jumbo_list_id_num = [(jumbo.type.id,jumbo.num) for jumbo in self.jumbo_list]
        try:
            index = self._jumbo_list_id_num.index((id,num))
        except ValueError:
            return None
        else:
            return self.jumbo_list[index]
   
class JumboSubPart:
    def __init__(self,x1=0,y1=0,x2=0,y2=0,jumbo=None,item=None):
        self.jumbo = jumbo
        self.item = item 
        self.x1 = x1
        self.y1 = y1
        self.x2 = x2
        self.y2 = y2

    def __str__(self):
        return ("JumboSubPart=("+str(self.x1)+","+str(self.y1)+","+str(self.x2)+","+str(self.y2)+")")
       
class CutTreeNode:
    def __init__(self,jumbo_sub_part,dir_cut="none",offset=0):
        self.jumbo_sub_part = jumbo_sub_part
        self.dir_cut = dir_cut
        self.offset = offset
        self.left = None
        self.right = None
        
    def __str__(self):
        if self.jumbo_sub_part==None:
            return "Node: jumbo_sub_part=None dir-cut="+self.dir_cut+" offset="+str(self.offset)+" left=["+str(self.left)+"] right=["+str(self.right)+"]"
        elif self.jumbo_sub_part.item==None:
            return "Node: jumbo_sub_part=(jumboid:"+str(self.jumbo_sub_part.jumbo.type.id)+"#"+str(self.jumbo_sub_part.jumbo.num)+") dir-cut="+self.dir_cut+" offset="+str(self.offset)+" left=["+str(self.left)+"] right=["+str(self.right)+"]"
        else:
            return "Node: item=(id:"+str(self.jumbo_sub_part.item.type.id)+"#"+str(self.jumbo_sub_part. item.num)+") dir-cut="+self.dir_cut+" offset="+str(self.offset)+" left=["+str(self.left)+"] right=["+str(self.right)+"]"
        
class CutTree:
    def __init__(self,jumbo):
        self.jumbo = jumbo
        self.root = None

    def __str__(self):
        return "Jumbo id:"+str(self.jumbo.type.id)+"#"+str(self.jumbo.num)+" cut-tree="+str(self.root)
    
    def get_max_depth(self):
        return self.get_depth(self.root)
       
    def get_depth(self,cut_tree):
        if cut_tree==None:
            return 0
        else:
            return 1+max(self.get_depth(cut_tree.left),self.get_depth(cut_tree.right))
    
    def build_cut_tree_from_json(self,input_data,problem_data,json_cut_tree):
        #create the initial sub part for the jumbo
        init_sub_part = JumboSubPart(0,0,self.jumbo.type.width,self.jumbo.type.height,self.jumbo,None)
        self.root = CutTree.build_cut_node_from_json(input_data,problem_data,json_cut_tree,init_sub_part)
    
    @classmethod
    def build_cut_node_from_json(cls,input_data,problem_data,json_cut_tree,sub_part)->CutTreeNode:
        #build the new node
        new_node = CutTreeNode(sub_part)
        item_id = -1
        item_num = -1
        item_type = None
        #try to find an id for the item
        try:
            item_id = int(json_cut_tree["item_id"])
        except KeyError:
            item_id = -1
        #try to find the type
        if item_id>=0:
            item_type = input_data.find_item_type(item_id)
            if item_type==None:
                raise Exception("DATA Error","unknown item type id \'" +str(item_id)+"\' in data\n")
            #get a item num:
            item_num = item_type.get_new_num()
            if item_num<0:
                #this error should be detected by ObjectType.get_new_num() before
                raise Exception("DATA Error","too many item with id \'" +str(item_id)+"\': nb= "+str(item_type.nb)+"\n")
            #rmq: we could also check if item id#num is not already in the solution...
        #find an offset for the cut
        try:
            offset = int(json_cut_tree["offset"])
        except KeyError:
            offset = -1 #means "no cut"
        new_node.offset = offset
        #find a cut direction
        try:
            dir_cut = json_cut_tree["dir-cut"]
        except KeyError:
            #default value = "vertical"
            dir_cut = "vertical"
            if offset!=-1: #only in case of cut
                print("DATA Warning: dir-cut set by defult to vertical")
        if dir_cut not in("vertical","horizontal"):
            raise Exception("DATA Error","dir_cut must be \'horizontal\'/\'vertical\'\n")
        new_node.dir_cut = dir_cut
        
        #get the item in problem data
        item = problem_data.find_item(item_id,item_num)
        #update the JumboSubPart
        sub_part.item = item
        if item!= None:
            item.jumbo_sub_part = sub_part
        #if offset>=0 there is a cut and then two nodes
        if new_node.offset>=0:
            #build 2 jumboSubPart
            if dir_cut=="vertical":    
                left_jumbo_sub_part = JumboSubPart(sub_part.x1,sub_part.y1,sub_part.x1+offset,sub_part.y2,sub_part.jumbo,None)
                right_jumbo_sub_part = JumboSubPart(sub_part.x1+offset+input_data.cut_width,sub_part.y1,sub_part.x2,sub_part.y2,sub_part.jumbo,None)
            else:
                left_jumbo_sub_part = JumboSubPart(sub_part.x1,sub_part.y1,sub_part.x2,sub_part.y1+offset,sub_part.jumbo,None)
                right_jumbo_sub_part = JumboSubPart(sub_part.x1,sub_part.y1+offset+input_data.cut_width,sub_part.x2,sub_part.y2,sub_part.jumbo,None)
            #left is in json?
            try:
                json_left = json_cut_tree["left"]
            except KeyError:
                #the left is not described in json : create a empty (without item) JumboSubPart
                new_node.left = CutTreeNode(left_jumbo_sub_part)
            else:
                new_node.left = CutTree.build_cut_node_from_json(input_data,problem_data,json_left,left_jumbo_sub_part)
            try:
                json_right = json_cut_tree["right"]
            except KeyError:
                new_node.right = CutTreeNode(right_jumbo_sub_part)
            else:
                new_node.right = CutTree.build_cut_node_from_json(input_data,problem_data,json_right,right_jumbo_sub_part)
        return new_node



def draw_cut_tree(cut_tree_node,startx,starty,width,size_factor):
    res_list = []
    if cut_tree_node!=None:
        current_sub_part = cut_tree_node.jumbo_sub_part
        current_item = current_sub_part.item
        if current_item!= None:
            item_x = startx+(current_sub_part.x1//size_factor)
            item_y = starty+(current_sub_part.y1//size_factor)
            item_width = (current_sub_part.x2-current_sub_part.x1)//size_factor
            item_height = (current_sub_part.y2-current_sub_part.y1)//size_factor
            res_list.append(svg.Rect(
                    x=item_x, y=item_y,
                    width=item_width, height=item_height,
                    stroke="black",
                    fill="blue",
                    stroke_width=1,
                ))
            item_text = "item "+str(current_item.type.id)+"#"+str(current_item.num)+"('"+current_item.type.name+"')"
            res_list.append(svg.Text(x=item_x+item_width//2, 
                y=item_y+item_height//2, 
                class_=["small"], 
                text=item_text))
        else:
            item_x = startx+(current_sub_part.x1//size_factor)
            item_y = starty+(current_sub_part.y1//size_factor)
            item_width = (current_sub_part.x2-current_sub_part.x1)//size_factor
            item_height = (current_sub_part.y2-current_sub_part.y1)//size_factor
            res_list.append(svg.Rect(
                    x=item_x, y=item_y,
                    width=item_width, height=item_height,
                    stroke="black",
                    fill="transparent",
                    stroke_width=1,
                ))
        #recall that [*L1,*L2] concatenets L1 and L2
        res_list = [*res_list,*draw_cut_tree(cut_tree_node.left,startx+width,starty,width,size_factor),*draw_cut_tree(cut_tree_node.right,startx+width,starty,width,size_factor)]
    return res_list

def search_leaves(cut_tree_node):
    L = []
    if cut_tree_node != None:
        if cut_tree_node.left==None and cut_tree_node.right==None:
            L.append(cut_tree_node.jumbo_sub_part)
        else:
            if cut_tree_node.left!=None:
                L = [*L,*search_leaves(cut_tree_node.left)]
            if cut_tree_node.right!=None:
                L = [*L,*search_leaves(cut_tree_node.right)]
    return L

class Solution:
    def __init__(self,input_data,problem_data,solution_file_name="cut_solution.json"):
        self.solution_file_name = solution_file_name
        self.cut_tree_list = []
        self.input_data = input_data
        self.problem_data = problem_data
        self.sub_part_list = []

    def generate_basic(self):
        #greedy strategy
        #make copies of lists
        item_list = self.problem_data.item_list[:]
        current_item_width = item_list[0].type.width
        current_item_height = item_list[0].type.height
        jumbo_list = self.problem_data.jumbo_list[:]
        current_jumbo_remainig_width = jumbo_list[0].type.width
        current_jumbo_remainig_height = jumbo_list[0].type.height

        #while not done...
        return

        #one could change the order...
        while len(item_list)>0:
            if current_jumbo_remainig_width>current_item_width.self.input_data.cut_width:
                pass
                #to do ....

    def write_json_file(self,file_name=None):
        pass
        #to do ....

        
    def __str__(self):
        return "operation list:"+str([str(tree) for tree in self.cut_tree_list])+" sub-part list="+str([str(p) for p in self.sub_part_list])
        
    def read_json_file(self,file_name=None):
        if file_name==None:
            file_name = self.solution_file_name
        else:
            self.solution_file_name = file_name
        try:
            json_text = ""
            with open(file_name,"r") as f_out:
                json_lines = f_out.readlines()
            for lines in json_lines:
                json_text += lines#.replace('\n','')
        except FileNotFoundError as msg:
            raise #call other exception handler before exit()
            print("Error: "+str(msg))
            exit()
        #build the cut tree list
        solution_json = json.loads(json_text)
        for json_jumbo in solution_json["op_list"]:
            try:
                jumbo_id = int(json_jumbo["jumbo_id"])
            except KeyError:
                raise Exception("DATA Error","jumbo id is missing in solution file\n")
            jumbo_type = self.input_data.find_jumbo_type(jumbo_id)

            if jumbo_type==None:
                raise Exception("DATA Error","unknown jumbo id#num \'" +str(jumbo_id)+"\' in solution file\n")
            jumbo_num = jumbo_type.get_new_num()
            cut_tree = CutTree(self.problem_data.find_jumbo(jumbo_id,jumbo_num))
            json_cut_tree = json_jumbo["cut-tree"]
            cut_tree.build_cut_tree_from_json(self.input_data,self.problem_data,json_cut_tree)
            self.cut_tree_list.append(cut_tree)
            
    def check(self):
        '''return a csv list of errors
        item_id ; item_num ; error_msg
        '''
        #item_list to check if all items are processed
        item_list = []
        waste_list = []
        error_list = []
        for cut_tree in self.cut_tree_list:
            sub_part_list = search_leaves(cut_tree.root)
            for sub_part in sub_part_list:
                if sub_part.item!=None:
                    item_list.append((sub_part.item.type.id,sub_part.item.num))
                    #check bounds of jumbo
                    bound_error = False
                    if (sub_part.x1>sub_part.jumbo.type.width or 
                        sub_part.x2>sub_part.jumbo.type.width or
                        sub_part.y1>sub_part.jumbo.type.height or
                        sub_part.y2>sub_part.jumbo.type.height):
                        bound_error = True
                    #check dimensions
                    dim1 = sub_part.x2-sub_part.x1
                    dim2 = sub_part.y2-sub_part.y1
                    dim_error = False
                    if dim1==sub_part.item.type.width:
                        if dim2!=sub_part.item.type.height:
                            dim_error = True
                    else:
                        if dim1==sub_part.item.type.height:
                            if dim2!=sub_part.item.type.width:
                                dim_error = True
                        else:
                            dim_error = True
                    if bound_error:
                        error_list.append(str(sub_part.item.type.id)+";"+str(sub_part.item.num)+";"+"bound error ("+str(sub_part.x1)+","+str(sub_part.y1)+str(sub_part.x2)+","+str(sub_part.y2)+")")
                    if dim_error:
                        error_list.append(str(sub_part.item.type.id)+";"+str(sub_part.item.num)+";"+"bad dimension ("+str(dim1)+"x"+str(dim2)+")")
                else: 
                    #it's a waste subpart
                    waste_list.append((sub_part.jumbo.num,(sub_part.x2-sub_part.x1)*(sub_part.y2-sub_part.y1)))
                    #check bounds of jumbo
                    bound_error = False
                    if (sub_part.x1>sub_part.jumbo.type.width or 
                        sub_part.x2>sub_part.jumbo.type.width or
                        sub_part.y1>sub_part.jumbo.type.height or
                        sub_part.y2>sub_part.jumbo.type.height):
                        bound_error = True
                    if bound_error:
                        error_list.append("waste sub part; in jumbo "+str(sub_part.jumbo.num)+";"+"bound error ("+str(sub_part.x1)+","+str(sub_part.y1)+str(sub_part.x2)+","+str(sub_part.y2)+")")
                   
        #check if all items in self.problem_data are in item_list
        for item in self.problem_data.item_list:
            if (item.type.id,item.num) not in item_list:
                error_list.append(str(item.type.id)+";"+str(item.num)+";"+"is missing")

        #compute the total surface of waste
        total_waste_surface = 0
        for part in waste_list:
            total_waste_surface += part[1]
                    
        print("------------------\n total waste = "+str(total_waste_surface))
        
        return error_list

    def get_max_cut_tree_depth(self):
        tree_depth = 0
        for cut_tree in self.cut_tree_list:
            tree_depth = max(tree_depth,cut_tree.get_max_depth())
            print("tree_depth = ",str(tree_depth))
        return tree_depth

    def draw(self,size_factor=20)->svg.SVG:
        total_max_width = 0
        total_max_height = 0
        margin = 10
        svg_elements = []

        for cut_tree in self.cut_tree_list:
            current_jumbo = cut_tree.root.jumbo_sub_part.jumbo
            jumbo_x = cut_tree.root.jumbo_sub_part.x1//size_factor+margin
            jumbo_y = total_max_height+cut_tree.root.jumbo_sub_part.y1//size_factor+margin
            jumbo_width = (cut_tree.root.jumbo_sub_part.x2-cut_tree.root.jumbo_sub_part.x1)//size_factor
            jumbo_height = (cut_tree.root.jumbo_sub_part.y2-cut_tree.root.jumbo_sub_part.y1)//size_factor
           
            sub_part_list = search_leaves(cut_tree.root)
            print(sub_part_list)

            for sub_part in sub_part_list:
                print(sub_part)
                current_item = sub_part.item
                if current_item!= None:
                    item_x = jumbo_x+(sub_part.x1//size_factor)
                    item_y = jumbo_y+(sub_part.y1//size_factor)
                    item_width = (sub_part.x2-sub_part.x1)//size_factor
                    item_height = (sub_part.y2-sub_part.y1)//size_factor
                    svg_elements.append(svg.Rect(
                        x=item_x, y=item_y,
                        width=item_width, height=item_height,
                        stroke="black",
                        fill="blue",
                        stroke_width=1,
                    ))
                    item_text = "item "+str(current_item.type.id)+"#"+str(current_item.num)+"('"+current_item.type.name+"')"
                    svg_elements.append(svg.Text(x=item_x+item_width//2, 
                        y=item_y+item_height//2, 
                        class_=["small"], 
                        text=item_text))
                else:
                    item_x = jumbo_x+(sub_part.x1//size_factor)
                    item_y = jumbo_y+(sub_part.y1//size_factor)
                    item_width = (sub_part.x2-sub_part.x1)//size_factor
                    item_height = (sub_part.y2-sub_part.y1)//size_factor
                    svg_elements.append(svg.Rect(
                        x=item_x, y=item_y,
                        width=item_width, height=item_height,
                        stroke="black",
                        fill="transparent",
                        stroke_width=1,
                    ))
            total_max_width = max(total_max_width,current_jumbo.type.width//size_factor+margin)
            total_max_height += current_jumbo.type.height//size_factor + margin
        svg_elements.append( svg.Style(text=dedent("""
                    .small { font: 10px sans-serif; }
                    .heavy { font: bold 30px sans-serif; }

                    /* Note that the color of the text is set with the    *
                    * fill property, the color property is for HTML only */
                    .Rrrrr { font: italic 40px serif; fill: red; }
                """)))

        return svg.SVG(
            width=total_max_width,
            height=total_max_height,
            elements=svg_elements
        )

    def draw_steps(self,size_factor=20) -> svg.SVG:
        total_max_width = 0
        total_max_height = 0
        margin = 10
        svg_elements = []
        for cut_tree in self.cut_tree_list:
            current_sub_part = cut_tree.root.jumbo_sub_part
            current_jumbo = current_sub_part.jumbo
            jumbo_x = current_sub_part.x1//size_factor+margin
            jumbo_y = total_max_height+current_sub_part.y1//size_factor+margin,
            jumbo_width = (current_sub_part.x2-current_sub_part.x1)//size_factor
            jumbo_height = (current_sub_part.y2-current_sub_part.y1)//size_factor

            list_svg_element = draw_cut_tree(cut_tree.root,jumbo_width+2*margin,total_max_height+margin,jumbo_width+margin,size_factor)
            for lse in list_svg_element:
                svg_elements.append(lse)
            total_max_width = max(total_max_width,current_jumbo.type.width//size_factor)
            total_max_height += current_jumbo.type.height//size_factor + margin
            svg_elements.append(svg.Rect(
                    x=jumbo_x, y=jumbo_y,
                    width=jumbo_width, height=jumbo_height,
                    stroke="black",
                    fill="transparent",
                    stroke_width=1,
                ))
            jumbo_text = "Initial jumbo "+str(current_jumbo.type.id)+"#"+str(current_jumbo.num)
            svg_elements.append(svg.Text(x=margin+jumbo_width//2, 
                y=total_max_height-margin, 
                class_=["small"], 
                text=jumbo_text))

        total_max_width += 2*margin
        total_max_width *= self.get_max_cut_tree_depth()
        svg_elements.append( svg.Style(text=dedent("""
                    .small { font: 10px sans-serif; }
                    .heavy { font: bold 30px sans-serif; }

                    /* Note that the color of the text is set with the    *
                    * fill property, the color property is for HTML only */
                    .Rrrrr { font: italic 40px serif; fill: red; }
                """)))

        return svg.SVG(
            width=total_max_width,
            height=total_max_height,
            elements=svg_elements
            #[
            #    svg.Rect(
            #        x=10, y=10,
            #        width=30, height=30,
            #        stroke="black",
            #        fill="transparent",
            #        stroke_width=5,
            #    ),
            #    svg.Line(
            #        x1=10, x2=50,
            #        y1=110, y2=150,
            #        stroke="orange",
            #        stroke_width=5,
            #    ),
            #    svg.Style(
            #    text=dedent("""
            #        .small { font: italic 13px sans-serif; }
            #        .heavy { font: bold 30px sans-serif; }
            #        /* Note that the color of the text is set with the    *
            #        * fill property, the color property is for HTML only */
            #        .Rrrrr { font: italic 40px serif; fill: red; }
            #    """),
            #    ),
            #    svg.Text(x=20, y=35, class_=["small"], text="My"),
            #],
        )

    def save_steps_svg(self,file_name="output_steps.svg",size_factor=20):
        try:
            with open(file_name,"w") as f_out:
                svg_text = self.draw_steps(size_factor)
                f_out.write(str(svg_text))
        except Exception as msg:
            raise #call other exception handler before exit()
            print("Error: "+str(msg))
            exit()

    def save_svg(self,file_name="output.svg",size_factor=20):
        try:
            with open(file_name,"w") as f_out:
                svg_text = self.draw(size_factor)
                f_out.write(str(svg_text))
        except Exception as msg:
            raise #call other exception handler before exit()
            print("Error: "+str(msg))
            exit()
            

if __name__=="__main__":

    if len(sys.argv) < 3:
        print('Usage: check_solution data_file.json solution_file.json')
        data_file_name = "input.json"
        solution_file_name = "output.json"
    else:
        data_file_name = sys.argv[1]
        solution_file_name = sys.argv[2]

    msg_file_name = "msg.csv"


    print("--------------------")
    print("reading "+data_file_name)
    input_data = InputData()
    input_data.read_json_file(data_file_name)
    print("input_data:\n",input_data)
    print("--------------------")
    problem_data = ProblemData(input_data)
    print("problem_data:\n",problem_data)
    print("--------------------")

    #basic_solution = Solution(input_data,problem_data)
    #basic_solution.generate_basic()
    #basic_solution.write_json_file("basic_solution.json")

    print("reading "+solution_file_name)
    solution = Solution(input_data,problem_data)
    solution.read_json_file(solution_file_name)
    solution.check()
    print("solution(after check):\n",solution)
    solution.save_steps_svg("solution_steps.svg",size_factor = 1)
    solution.save_svg("solution.svg",size_factor = 1)
    msg_list = solution.check()

    #save msg as CSV file
    try:
        with open(msg_file_name,"w") as f_out:
            f_out.write("id ; num ; msg\n")
            for line in msg_list:
                f_out.write(line+"\n")
    except Exception as msg:
        raise #call other exception handler before exit()
        print("Error: "+str(msg))
        exit()
    print("--------------------")
    
    print("Error writen in file '"+msg_file_name+"'\n")
    print("--------------------")




