

export interface OrderItem {
  id?: number;
  product: {id: number; name?:string; price?: number };
  quantity: number;
  price? : number;
  
}
