package aca.classifier;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.io.*;

import aca.main.encoder;

public class SVMClassifier implements Classifier {
	
	public double compute_ic(String text)
	{
		HashMap<Character,Integer> h_map=new HashMap<Character,Integer>();
		String text_l=text.toLowerCase();
		for(char c: text_l.toCharArray())
		{
			Character cur_c=Character.valueOf(c);
			if(h_map.containsKey(cur_c))
			{
				h_map.put(cur_c, h_map.get(cur_c)+1);
			}
			else
			{
				h_map.put(cur_c, 1);
			}
		}
		int total_len=text.length();
		if(total_len<=1)
		{
			//denominator=0
			System.err.println("The total length is less than 2. Unable to compute ic");
		}
		int numerator=0;
		for(Entry<Character, Integer> entry: h_map.entrySet())
		{
			int cur_num=entry.getValue();
			if(cur_num==1)
			{
				continue;
			}
			else
			{
				numerator+=cur_num*(cur_num-1);
			}
		}
		int denominator=total_len*(total_len-1);
		return (double)numerator/(double)denominator;
	/*	int numerator=0;
		int text_len=text.length();
		int[] char_cnt=new int[26];
		for(char c:text.toLowerCase().toCharArray())
		{
			char_cnt[c-'a']+=1;
		}
		for(int i=0;i<26;i++)
		{
		   if(char_cnt[i]>1)
		   {
			   numerator+=char_cnt[i]*(char_cnt[i]-1);
		   }
		}
		if(numerator==0)
			return 0.0;
		double denominator=(double)(text_len*(text_len-1))/(double)26;
		if(denominator==0)
		{
			System.err.println("Error in computing the ic for "+text);
		}
		return (double)numerator/denominator;*/
		
	}
	
	public double compute_max_ic(String text)
	{
		int total_len=text.length();
		double max_ic=0.0;
		for(int p=1;p<=15;p++)
		{
			int group_size=total_len/p;
			for(int i=0;i<text.length();i+=group_size)
			{
				int end=i+group_size>=text.length()?text.length():i+group_size;
				String group_str=text.substring(i,end);
				double cur_ic=compute_ic(group_str);
				if(cur_ic>max_ic)
				{
					max_ic=cur_ic;
				}
			}
		}
		return max_ic;
	}
	
	public double compute_kappa(String text,String comp_text)
	{
		assert(text.length()==comp_text.length());
		int overlap=0;
		for(int i=0;i<text.length();i++)
		{
			if(text.charAt(i)==comp_text.charAt(i))
			{
				overlap++;
			}
		}
		return (double)overlap/(double)text.length();
		
	}
	
	public double get_max_kappa(String text)
	{
		double max_kappa=0.0;
		for(int i=1;i<26;i++)
		{
			//shift to right
			String new_str=text.substring(i);
			new_str+=text.substring(0, i);
			double kappa=compute_kappa(text,new_str);
			if(kappa>max_kappa)
			{
				max_kappa=kappa;
			}
		}
		return max_kappa;
		
	}
	
	public double get_max_ic_shift(String text)
	{
		double max_ic=0.0;
		for(int i=1;i<26;i++)
		{
			String new_str=text.substring(i);
			new_str+=text.substring(0, i);
			double cur_ic=compute_ic(new_str);
			if(cur_ic>max_ic)
			{
				max_ic=cur_ic;
			}
		}
		return max_ic;
	}
	
	public double compute_dic(String text)
	{
		//only consider alphabetic bigrams
		int total_dl=0;
		HashMap<String,Integer> h_map=new HashMap<String,Integer>();
		String text_l=text.toUpperCase();
		for(int i=0;i<text_l.length();i++)
		{
			char cur_c=text_l.charAt(i);
			if(cur_c<='Z' && cur_c>='A')
			{
				if(i==text_l.length()-1)
					break;
				char next_c=text_l.charAt(i+1);
				if(next_c<='Z' && next_c>='A')
				{
					StringBuilder sb=new StringBuilder();
					sb.append(cur_c);
					sb.append(next_c);
					String cur_di=sb.toString();
					if(h_map.containsKey(cur_di))
					{
						int count=h_map.get(cur_di);
						h_map.put(cur_di, count+1);
					}
					else
					{
						h_map.put(cur_di, 1);
					}
					total_dl+=1;
				}
				else
				{
					continue;
				}
			}
			else
			{
				continue;
			}
		}
		int numerator=0;
		for(Entry<String, Integer> entry: h_map.entrySet())
		{
			int cur_num=entry.getValue();
			if(cur_num==1)
			{
				continue;
			}
			else
			{
				numerator+=cur_num*(cur_num-1);
			}
		}
		int denominator=total_dl*(total_dl-1);
		return (double)numerator/(double)denominator;
		
	}
	
    /**
     * Compute the square root of the percentage of 3 character repeats (not necessary in consecutive position)
     * This is described in �Identifying Fractionated Morse� in the MJ2002 issue of the Cryptogram.
     * Reference: http://home.comcast.net/~acabion/acarefstats.html
     * 
     * @param text the ciphertext
     * @return the lr value
     */
	public double compute_lr(String text)
	{
		int lr=0;
		for(int i=0;i<text.length();i++)
		{
			int cur_count=0;
			char cur_c=text.charAt(i);
			for(int j=i+1;j<text.length();j++)
			{
				char c=text.charAt(j);
				if(c==cur_c)
				{
					cur_count++;
				}
				if(cur_count>=3)
				{
					lr+=1;
					break;
				}
			}
		}
		return (double)lr/(double)text.length();
	}
	
	public boolean contain_j(String text)
	{
		for(char c:text.toLowerCase().toCharArray())
		{
			if(c=='j')
			{
				return true;
			}
		}
		return false;
	}
	
	public HashMap<Character,Integer> get_freq(String text)
	{
		HashMap<Character,Integer> map=new HashMap<Character,Integer>();
		for(char c:text.toLowerCase().toCharArray())
		{
			if(map.containsKey(c))
			{
				int cur_feq=map.get(c);
				map.put(c, cur_feq+1);
			}
			else
			{
				map.put(c, 1);
			}
		}
		return map;
	}
	
	public void read_uni_prob(String prob_file)
	{
		try
		{
			BufferedReader br=new BufferedReader(new FileReader(prob_file));
			String line;
			while((line=br.readLine())!=null)
			{
				if(line.startsWith("#"))
					continue;
				else
				{
					String[] parts=line.trim().split("\t");
					assert(parts.length==2);
					Character cur=Character.valueOf(parts[0].charAt(0));
					Double value=Double.parseDouble(parts[1]);
					uni_probs.put(cur, value);
				}
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	private HashMap<Character,Double> uni_probs=new HashMap<Character,Double>();
	
	private boolean all_number(String text)
	{
		for(char c:text.toCharArray())
		{
			if(c<'0' || c>'9')
				return false;
		}
		return true;
	}
	
	private int get_start_num(String text)
	{
		int num=0;
		for(char c:text.toCharArray())
		{
			if(c<'0' || c>'9')
				break;
			else
				num++;
		}
		return num;
	}
	
	private int get_end_num(String text)
	{
		int num=0;
		for(int i=text.length()-1;i>=0;i--)
		{
			char c=text.charAt(i);
			if(c<'0' || c>'9')
				break;
			else
				num++;
		}
		return num;
	}
	
	public boolean even_len(String text)
	{
		String[] parts=text.split(" ");
		int sum=0;
		for (int i=0;i<parts.length;i++)
		{
			sum+=parts[i].length();
		}
		if(sum%2==0)
			return true;
		else
		    return false;
	}
    
	public SVMFeature extract_feature(String text)
	{
		SVMFeature f=new SVMFeature();
		if(contain_j(text))
		{
			f.has_j=true;
		}
		else
		{
			f.has_j=false;
		}
		if(all_number(text))
		{
			f.all_num=true;
		}
		else
		{
			f.all_num=false;
		}
		if(text.contains("#"))
		{
			f.contain_star=true;
		}
		else
		{
			f.contain_star=false;
		}
		double ic=compute_ic(text);
		f.ic=ic;
		// HashMap<Character,Integer> frequencies=
		f.frequencies=get_freq(text);
		f.length=text.length();
		//if(text.length()%2==0)
		if(even_len(text))
		{
			f.length_even=true;
		}
		else
		{
			f.length_even=false;
		}
		f.uni_prob=compute_uni_prob(text);
		//only if f.part_num is true
		f.start_digit_num=get_start_num(text);
		f.end_digit_num=get_end_num(text);
		double kappa=get_max_kappa(text);
		f.max_kappa=kappa;
		double lr=compute_lr(text);
		f.lr_value=lr;
		f.max_ic=compute_max_ic(text);
		f.max_ic2=get_max_ic_shift(text);
		f.half_percentage=compute_half_percent(text);
	//	f.bigram_derivation=compute_bigram_dv(text,)
		return f;
	}
	
	public double compute_half_percent(String text)
	{
		int am_cnt=0;
		int nz_cnt=0;
		for(char c:text.toLowerCase().toCharArray())
		{
			if(c<'a' || c>'z')
				continue;
			if(c<'n')
				am_cnt++;
			else
				nz_cnt++;
		}
		return (double)am_cnt/(double)nz_cnt;
	}
	
	public boolean len_25(String text)
	{
		String[] parts=text.split(" ");
		int sum=0;
		for (int i=0;i<parts.length;i++)
		{
			sum+=parts[i].length();
		}
		if(sum%25==0)
			return true;
		else
		    return false;
	}
	
	public double compute_uni_prob(String text)
	{
		String text_low=text.toLowerCase();
		//double result=1.0;
		double result=0.0;
		int total_len=0;
		for(char c:text_low.toCharArray())
		{
			//result*=uni_probs.get(c);
			if(c<'a' || c>'z')
			{
				continue;
			}
			result+=Math.log(uni_probs.get(Character.valueOf(c)));
			total_len+=1;
		}
		if(total_len==0)
			return 0.0;
		result/=total_len;
		
		return result;
	}
	
	public ArrayList<Double> build_feature_vec(SVMFeature f)
	{
		ArrayList<Double> vec_feat=new ArrayList<Double>();
		if(f.has_j)
		{
			vec_feat.add(1.0);
			vec_feat.add(0.0);
		}
		else
		{
			vec_feat.add(0.0);
			vec_feat.add(1.0);
		}
		if(f.length_even)
		{
			vec_feat.add(1.0);
			vec_feat.add(0.0);
		}
		else
		{
			vec_feat.add(0.0);
			vec_feat.add(1.0);
		}
		if(f.all_num)
		{
			vec_feat.add(1.0);
			vec_feat.add(0.0);
		}
		else
		{
			vec_feat.add(0.0);
			vec_feat.add(1.0);
		}
		if(f.contain_star)
		{
			vec_feat.add(1.0);
			vec_feat.add(0.0);
		}
		else
		{
			vec_feat.add(0.0);
			vec_feat.add(1.0);
		}
		vec_feat.add(f.ic);
		//vec_feat.add((double)f.length);
		for (int i=0;i<26;i++)
		{
			char cur_char=(char)('a'+i);
			if(f.frequencies.containsKey(cur_char))
			{
				int c_freq=f.frequencies.get(cur_char);
			    vec_feat.add((double)c_freq/(double)f.length);//unigram probabilities
			}
			else
			{
				vec_feat.add(0.0);
			}
		}
		//0304 add the unigram prob
		vec_feat.add(f.uni_prob);
		//0427 add the start num and end num
		vec_feat.add((double)f.start_digit_num/(double)f.length);
		vec_feat.add((double)f.end_digit_num/(double)f.length);
		return vec_feat;
	}
	
	public ArrayList<Double> scale_feat(ArrayList<Double> f)
	{
		ArrayList<Double> result=new ArrayList<Double>();
		double highest=0.0;
		for(int i=0;i<f.size();i++)
		{
			if(f.get(i)>highest)
			{
				highest=f.get(i);
			}
		}
		for(int i=0;i<f.size();i++)
		{
			result.add(f.get(i)/highest);
		}
		return result;
	}
	
	
	
	public void print_training(String tag,ArrayList<Double> features, BufferedWriter bw)
	{
		//for(int i=0;i<features.size();i++)
		//{
		  StringBuilder sb=new StringBuilder();
		  try
		  {
			HashMap<String,Integer> encoder_dict=encoder.build_dict_map(encoder.encoder_list);
			
		    for(int i=0;i<features.size();i++)
		    {
			   sb.append(Integer.toString(i+1)+":"+Double.toString(features.get(i))+" ");
		    }
		  //  if (encoder.encoder_list)
		    //convert tag to id
		 //   int tag_id=encoder.get_encoder_index(tag);
		    int tag_id=encoder.get_encoder_index(encoder_dict, tag);
		    if(tag_id==-1)
		    {
		    	System.err.println("Invalid tag");
		    }
		    String to_print=Integer.toString(tag_id)+" "+sb.toString().trim();
		    bw.write(to_print+"\n");
		  }
		  catch(Exception e)
		  {
			  System.err.println(e.getMessage());
		  }
		  //bw.write(sb.toString());
		  //bw.write("\n");
		//}
		
		
		
	//	if (encoder.encoder_list.)
		
		
	}
	
	/*public void print_test(String tag,String encipher_text)
	{
		
	}*/
	
	//digraph incidence of coincidence
/*	public double get_dic(String text)
	{
		int text_len=text.length();
		if(text_len%2!=0)
		{
			return 0.0;
		}
		HashMap<String,Integer> di_count=new HashMap<String,Integer>();
		for(int i=0;i<text_len;i+=2)
		{
			String digraph=text.substring(i,i+2);
			if(di_count.containsKey(digraph))
			{
				int cur_cnt=di_count.get(digraph);
			    di_count.put(digraph, cur_cnt+1);
			}
			else
			{
				di_count.put(digraph,1);
			}
		}
		int numerator=0;
		for(String k:di_count.keySet())
		{
			int cnt=di_count.get(k);
			if(cnt>1)
			{
				numerator+=cnt*(cnt-1);
			}
		}
		if(numerator==0)
			return 0.0;
		double denominator=(double)(text_len)*(double)(text_len-1)/(double)(text_len/2);
		return (double)numerator/denominator;
		
	}*/
	
	
	
}
