/**
 *  Jpeg processing component excluding jpeg decoder itself.
 *  After jpeg decoder terminates, 3 files (one for each component) are created.
 *  
 *  This package contains components for further processing actions 
 *  (component extension, rotation, resizing, bmp assembling). 
 *  
 *  Plus to this actions, here would be placed components for file system 
 *  scanning(root dir would be set; root dir would contain files of different extension,
 *  only jpeg files must be selected and transformed to bmp).
 *  
 *  File system scanning process must consider files that have already been processed.
 *  If for given input jpeg exists output bmp no processing must be initiated.
 */

package processor;