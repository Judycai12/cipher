package aca.ciphers;

import java.util.ArrayList;
//import java.util.Random;

import aca.util.Generic_Func;

public class Period_Gromark extends Gromark{
	
	public Period_Gromark()
	{
		key="ENIGMA";
	}
	
	public Period_Gromark(String k)
	{
		key=k;
	}
	
	
	
	private String key;
	public String encode(String plain)
	 {
		int[] key_num=Cadenus.generate_order(key);
	//	int last_digit=key_num[key_num.length-1];
		char[][] trans_block=Gromark.build_trans_block(key_num,key);
		char[] keyed_alphabet=build_keyed_alphabet(trans_block,key_num);
		//int primer=get_num(key_num);
		//int[] num_k=generate_num_k(plain,primer);
		
		String plain_u=plain.toUpperCase();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<key_num.length;i++)
		{
			sb.append(key_num[i]);
		}		
		int key_len=key.length();
		int key_index=0;
		//int last=0;
		for(int i=0;i<plain.length();i+=key_len)
		{
			int end=i+key.length()>plain.length()?plain.length():i+key_len;
			//last=end-i;
			//String sub_str=plain.substring(i,end);
			int alphabet_index=Generic_Func.find_char(keyed_alphabet, key.charAt(key_index));
			sb.append(get_ct(plain_u,key_num,i,end,keyed_alphabet,alphabet_index));
			if(key_index==key.length()-1)
			{
				key_index=0;
			}
			else
			{
				key_index++;
			}
			if(end==plain.length())
			{
				sb.append(key_num[end-i-1]);
			}
			else
			{
			   update_num_k(key_num);
			}
			
		}
		//sb.append(key_num[last]);
		//Random r=new Random();
		//int key_len=1+r.nextInt(7);
		//sb.append(last_digit);
		return sb.toString();
	 }
	
	private void update_num_k(int[] num_k)
	{
		for(int i=0;i<num_k.length;i++)
		{
			int new_val=num_k[i];
			if(i==num_k.length-1)
			{
				new_val+=num_k[0];
			}
			else
			{
				new_val+=num_k[i+1];
			}
	        new_val=new_val%10;
	        num_k[i]=new_val;
		}
	}
	
	private String get_ct(String str,int[] num_key,int start,int end,char[] keyed_alphabet,int add_index)
	{
		StringBuilder sb=new StringBuilder();
	     for(int i=start;i<end;i++)
	     {
	    	 int p_loc=str.charAt(i)-'A';//assert plain is uppercase only
	    	// int key_loc=Generic_Func.find_char(keyed_alphabet, str.charAt(i));
	    	 int ct_pos=(p_loc+add_index+num_key[i-start])%26;
	    	 sb.append(keyed_alphabet[ct_pos]);
	     }
	     return sb.toString();
	}
	
	
	
	public static int get_num(int[] key_num)
	{
		//fail to check the overflow
		int len=key_num.length;
		int result=0;
		for(int i=0;i<len;i++)
		{
			result=result*10+key_num[i];
		}
		return result;
	}
	    
	    /*
	     * Decode the cipher text.
	     */
	    public String decode(String cipher)
	    {
	    	return null;
	    }
	    
	    public boolean key_need()
	    {
	    	return true;
	    }
	    
	    public int get_key_num()
	    {
	    	return 1;
	    }
	    
	    public ArrayList<Integer> get_key_len()
	    {
	    	return null;
	    }
}
