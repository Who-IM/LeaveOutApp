package board;

import java.util.*;

public class UtilMgr {

	 public String getContent(String comment){
        return br("", "<br>", comment);
   }

  public String br(String first, String brTag, String comment){
        StringBuffer buffer = new StringBuffer();
        StringTokenizer st = new StringTokenizer(comment, "\n");
        int count = st.countTokens();
        buffer.append(first);
        int i = 1;
         while(st.hasMoreTokens()){ 
           if(i==count){
             buffer.append(st.nextToken());
           }else{ 
             buffer.append(st.nextToken()+ brTag);
		   }
           i++;
         }
       return buffer.toString(); 
  } 
}
